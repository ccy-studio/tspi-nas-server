package com.saisaiwa.tspi.nas.common.file;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
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
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @date: 2024/03/12 15:57
 * @author: saisiawa
 **/
@Component
@Slf4j
public class FileLocalScanTask {

    @Resource
    private BucketsMapper bucketsMapper;

    @Resource
    private FileObjectMapper fileObjectMapper;

    @Resource
    private FileObjectService fileObjectService;

    private final FileAlterationMonitor monitor;

    private final Map<Buckets, FileAlterationObserver> observerMap = new ConcurrentHashMap<>();

    public FileLocalScanTask() {
        this.monitor = new FileAlterationMonitor(1000);
    }


    /**
     * 初始化读取全部存储桶开启监听
     */
    public void initListener() {
        List<Buckets> buckets = bucketsMapper.selectList(Wrappers.lambdaQuery(Buckets.class)
                .eq(Buckets::getIsDelete, 0));
        buckets.forEach(this::addListener);
        startListenerAll();
    }

    /**
     * 扫描全部的存储桶文件信息
     */
    public void scanAllBuckets() {
        List<Buckets> buckets = bucketsMapper.selectList(Wrappers.lambdaQuery(Buckets.class)
                .eq(Buckets::getIsDelete, 0));
        buckets.forEach(this::scanFileDiffAndFix);
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
            log.error("scanFileDiffAndFix 此bucket被移除文件不存在");
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
        FileObject rootObject = fileObjectMapper.getRootObject(buckets.getId());
        updateLopFile(files, dbFileObjects, rootObject);
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
                        insertFile.setFilePath(fileObjectService.getPath(search.get().getFilePath(), f.getName()));
                        insertFile.setRealPath(f.getAbsolutePath());
                        insertFile.setIsDir(false);
                        insertFile.setFileContentType(FileUtil.extName(f));
                        insertFile.setFileSize(FileUtil.size(f, true));
                        insertFile.setFileMd5(SecureUtil.md5(f));
                        insertFile.setIsDelete(0L);
                        insertFile.setCreateTime(LocalDateTime.now());
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
        fileObject.setFilePath(fileObjectService.getPath(parent.getFilePath(), file.getName()));
        fileObject.setRealPath(file.getAbsolutePath());
        fileObject.setIsDir(true);
        fileObject.setIsDelete(0L);
        fileObject.setCreateTime(LocalDateTime.now());
        return fileObject;
    }
}
