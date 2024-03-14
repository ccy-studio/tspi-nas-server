package com.saisaiwa.tspi.nas.common.file;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.saisaiwa.tspi.nas.domain.entity.Buckets;
import com.saisaiwa.tspi.nas.domain.entity.FileObject;
import com.saisaiwa.tspi.nas.mapper.BucketsMapper;
import com.saisaiwa.tspi.nas.mapper.FileObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @description:
 * @date: 2024/03/12 15:57
 * @author: saisiawa
 **/
@Component
@Slf4j
public class FileLocalScanService {

    @Resource
    private BucketsMapper bucketsMapper;

    @Resource
    private FileObjectMapper fileObjectMapper;

    @Resource
    private FileNativeService fileNativeService;

    private final FileAlterationMonitor monitor;

    private final Tika tika = new Tika();

    private final Map<Buckets, FileAlterationObserver> observerMap = new ConcurrentHashMap<>();

    public FileLocalScanService() {
        this.monitor = new FileAlterationMonitor(1000);
    }


    /**
     * 初始化读取全部存储桶开启监听
     */
    public void initListener() {
        List<Buckets> buckets = bucketsMapper.selectList(Wrappers.lambdaQuery(Buckets.class)
                .eq(Buckets::getIsDelete, 0));
        if (buckets.isEmpty()) {
            return;
        }
        buckets.forEach(this::addListener);
        startListenerAll();
    }

    /**
     * 扫描全部的存储桶文件信息
     */
    public void scanAllBuckets() {
        List<Buckets> buckets = bucketsMapper.selectList(Wrappers.lambdaQuery(Buckets.class)
                .eq(Buckets::getIsDelete, 0));
        if (buckets.isEmpty()) {
            return;
        }
        List<Future<Boolean>> futures = new ArrayList<>();
        log.info("ScanAllBucket DifferentFile");
        buckets.forEach(v -> {
            Future<Boolean> future = ThreadUtil.execAsync(() -> {
                scanFileDiffAndFix(v);
                return true;
            });
            futures.add(future);
        });
        log.info("ScanAllBucket TaskCount{}", futures.size());
        for (int i = 0; i < futures.size(); i++) {
            try {
                futures.get(i).get();
                log.info("ScanAllBucket Item Done「{}」", i + 1);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("ScanAllBucket Successful");
    }

    public void stopListenerAll() {
        try {
            monitor.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void startListenerAll() {
        if (observerMap.isEmpty()) {
            return;
        }
        try {
            monitor.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加一个监听器
     *
     * @param buckets
     */
    public void addListener(Buckets buckets) {
        if (!observerMap.containsKey(buckets)) {
            File file = new File(buckets.getMountPoint());
            if (!file.exists() || !file.isDirectory()) {
                log.error("无法给Bucket-{}添加监听器,因为文件不存在", buckets.getBucketsName());
                return;
            }
            FileAlterationObserver observer = new FileAlterationObserver(new File(buckets.getMountPoint()));
            observer.addListener(new FileListener(buckets));
            observerMap.put(buckets, observer);
            monitor.addObserver(observer);
        }
    }

    /**
     * 移除一个监听器
     *
     * @param buckets
     */
    public void removeListener(Buckets buckets) {
        if (observerMap.containsKey(buckets)) {
            monitor.removeObserver(observerMap.get(buckets));
            observerMap.remove(buckets);
        }
    }

    /**
     * 主动扫描差异文件并自动修复
     *
     * @param buckets 存储桶
     */
    public void scanFileDiffAndFix(Buckets buckets) {
        File file = new File(buckets.getMountPoint());
        if (!file.exists() || !file.isDirectory()) {
            log.error("scanFileDiffAndFix 此bucket（{}）被移除，因为文件不存在", buckets.getBucketsName());
            buckets.setIsDelete(buckets.getId());
            buckets.setUpdateTime(LocalDateTime.now());
            bucketsMapper.updateById(buckets);
            return;
        }
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            //空文件夹
            fileObjectMapper.deleteByBucketsId(buckets.getId());
            return;
        }
        //查询数据库中的文件
        List<FileObject> dbFileObjects = fileObjectMapper.getListByBucketId(buckets.getId());
        final List<Long> delIds = new ArrayList<>();
        // 查询是否被删除了
        for (FileObject object : dbFileObjects) {
            if (!FileUtil.exist(object.getRealPath())) {
                delIds.add(object.getId());
            }
        }
        if (!delIds.isEmpty()) {
            fileObjectMapper.deleteBatchIds(delIds);
            // 移除被删掉的数据
            dbFileObjects.removeIf(v -> delIds.contains(v.getId()));
        }

        FileObject rootObject = fileObjectMapper.getRootObject(buckets.getId());
        updateLopFile(files, dbFileObjects, rootObject);
        //更新数组内的逻辑
        delIds.clear();
        delIds.addAll(dbFileObjects.parallelStream()
                .filter(v -> v.getIsDelete() != 0 && v.getId() != null)
                .map(FileObject::getId)
                .toList());
        fileObjectMapper.deleteBatchIds(delIds);
        log.info("Bucket：{} 删除{}个", buckets.getBucketsName(), delIds.size());

        List<FileObject> saveList = dbFileObjects.parallelStream().filter(v -> v.getId() == null && v.getIsDelete() == 0)
                .toList();
        saveList.forEach(fileObjectMapper::insert);
        log.info("Bucket：{} 新增{}个", buckets.getBucketsName(), saveList.size());
    }

    /**
     * 文件扫描逻辑
     *
     * @param files
     * @param dbFileObjects
     * @param root
     */
    private void updateLopFile(File[] files, List<FileObject> dbFileObjects, FileObject root) {
        for (File f : files) {
            if (f.isDirectory()) {
                //目录
                //判断目录是否存在
                Optional<FileObject> search = dbFileObjects.parallelStream()
                        .filter(v -> v.getIsDelete() == 0 && v.getIsDir() && v.getRealPath().equals(f.getAbsolutePath()))
                        .findFirst();
                if (search.isPresent()) {
                    //目录存在
                    File[] dirFiles = f.listFiles();
                    if (dirFiles == null || dirFiles.length == 0) {
                        //如果这个目录下没有任何的文件
                        //删除这个目录下所有的子文件
                        for (FileObject object : dbFileObjects) {
                            if (object.getRealPath().startsWith(f.getAbsolutePath())) {
                                object.setIsDelete(object.getId());
                            }
                        }
                    } else {
                        //否则扫描此文件夹下的文件
                        updateLopFile(dirFiles, dbFileObjects, search.get());
                    }
                } else {
                    //不存在的目录
                    //要反推一致找到
                    File tf = f;
                    List<File> fArr = new ArrayList<>();
                    FileObject insertObject = null;
                    while (!tf.getAbsolutePath().equals(root.getRealPath())) {
                        String absolutePath = tf.getAbsolutePath();
                        search = dbFileObjects.parallelStream()
                                .filter(v -> v.getIsDelete() == 0 && v.getIsDir() && v.getRealPath().equals(absolutePath))
                                .findFirst();
                        if (search.isPresent()) {
                            //找到了
                            insertObject = search.get();
                            break;
                        }
                        fArr.add(tf);
                        tf = tf.getParentFile();
                    }
                    if (insertObject == null) {
                        insertObject = root;
                    }
                    for (File file : ListUtil.reverse(fArr)) {
                        insertObject = createDir(file, insertObject);
                        fileObjectMapper.insert(insertObject);
                        dbFileObjects.add(insertObject);
                        File[] dirFiles = file.listFiles();
                        if (dirFiles != null && dirFiles.length != 0) {
                            updateLopFile(dirFiles, dbFileObjects, insertObject);
                        }
                    }
                }
            }
        }
        for (File f : files) {
            if (!f.isDirectory()) {
                //是个文件
                Optional<FileObject> search = dbFileObjects.parallelStream()
                        .filter(v -> v.getIsDelete() == 0 && !v.getIsDir() && v.getRealPath().equals(f.getAbsolutePath()))
                        .findFirst();
                if (search.isEmpty()) {
                    //不存在
                    search = dbFileObjects.parallelStream()
                            .filter(v -> v.getIsDelete() == 0 && v.getIsDir() && v.getRealPath().equals(f.getParentFile().getAbsolutePath()))
                            .findFirst();
                    if (search.isEmpty()) {
                        log.error("错误：没有找到父文件:175");
                    } else {
                        FileObject insertFile = new FileObject();
                        insertFile.setFileName(f.getName());
                        insertFile.setParentId(search.get().getId());
                        insertFile.setBucketsId(search.get().getBucketsId());
                        insertFile.setFilePath(fileNativeService.getPath(search.get().getFilePath(), f.getName()));
                        insertFile.setRealPath(f.getAbsolutePath());
                        insertFile.setIsDir(false);
                        insertFile.setFileSize(FileUtil.size(f, true));
                        insertFile.setFileMd5(SecureUtil.md5(f));
                        insertFile.setIsDelete(0L);
                        insertFile.setCreateTime(LocalDateTime.now());
                        try {
                            insertFile.setFileContentType(tika.detect(f));
                        } catch (IOException e) {
                            log.error("获取文件ContentType错误", e);
                            insertFile.setFileContentType("application/octet-stream");
                        }
                        fileObjectMapper.insert(insertFile);
                        dbFileObjects.add(insertFile);
                    }
                }
            }
        }
    }

    private FileObject createDir(File file, FileObject parent) {
        if (parent.getId() == null) {
            throw new RuntimeException("id not null");
        }
        FileObject fileObject = new FileObject();
        fileObject.setFileName(file.getName());
        fileObject.setParentId(parent.getParentId());
        fileObject.setBucketsId(parent.getBucketsId());
        fileObject.setFilePath(fileNativeService.getPath(parent.getFilePath(), file.getName()));
        fileObject.setRealPath(file.getAbsolutePath());
        fileObject.setIsDir(true);
        fileObject.setIsDelete(0L);
        fileObject.setCreateTime(LocalDateTime.now());
        return fileObject;
    }
}
