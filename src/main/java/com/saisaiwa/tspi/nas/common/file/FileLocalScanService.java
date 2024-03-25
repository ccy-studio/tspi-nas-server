package com.saisaiwa.tspi.nas.common.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.core.toolkit.MybatisBatchUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.saisaiwa.tspi.nas.domain.entity.Buckets;
import com.saisaiwa.tspi.nas.domain.entity.FileObject;
import com.saisaiwa.tspi.nas.mapper.BucketsMapper;
import com.saisaiwa.tspi.nas.mapper.FileObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

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

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private SqlSessionFactory sqlSessionFactory;


    public FileLocalScanService() {
        this.monitor = new FileAlterationMonitor(2000);
    }


    /**
     * 初始化读取全部存储桶开启监听
     */
    public void initListener() {
        List<Buckets> buckets = bucketsMapper.selectList(Wrappers.lambdaQuery(Buckets.class)
                .eq(Buckets::getIsDelete, 0));
        startListenerAll();
        if (buckets.isEmpty()) {
            return;
        }
        buckets.forEach(this::addListener);
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
            bucketsMapper.deleteById(buckets.getId());
            return;
        }
        //文件是存在的
        File[] files = file.listFiles();
        if (files == null || files.length == 0) {
            //空文件夹
            fileObjectMapper.deleteByBucketsId(buckets.getId());
            return;
        }
        //查询数据库中的文件-查询的全部数据
        List<FileObject> dbFileObjects = fileObjectMapper.getListByBucketId(buckets.getId());
        //标记要删除的列表ID集合
        final List<Long> delIds = new ArrayList<>();
        // 查询是否被删除了
        for (FileObject object : dbFileObjects) {
            //如果是根目录就忽略
            if (object.getFilePath().equals(FileNativeService.DELIMITER)) {
                continue;
            }
            if (!FileUtil.exist(object.getRealPath())) {
                delIds.add(object.getId());
            }
        }
        if (!delIds.isEmpty()) {
            fileObjectMapper.deleteBatchIds(delIds);
            // 移除被删掉的数据
            dbFileObjects.removeIf(v -> delIds.contains(v.getId()));
        }
        if (dbFileObjects.isEmpty()) {
            return;
        }
        //获取根目录
        FileObject rootObject = fileObjectMapper.getRootObject(buckets.getId());
        if (rootObject == null) {
            //如果发现根目录为空那么清空数据表结束方法，同步任务交给监听器去做
            rootObject = new FileObject();
            rootObject.setIsDir(true);
            rootObject.setFileName(FileNativeService.DELIMITER);
            rootObject.setFilePath(FileNativeService.DELIMITER);
            rootObject.setRealPath(buckets.getMountPoint());
            rootObject.setBucketsId(buckets.getId());
            rootObject.setIsDelete(0L);
            fileObjectMapper.deleteByBucketsId(buckets.getId());
            fileObjectMapper.insert(rootObject);
            return;
        }
        final Long rootId = rootObject.getId();
        dbFileObjects.removeIf(v -> v.getId().equals(rootId));

        //扫描所有的文件夹类型的数据 <<---------------------------------------
        AtomicLong folderCounter = new AtomicLong(0);
        updateLopFile(files, dbFileObjects, rootObject, true);
        //1. 筛选出tempUid不为空的也就是所有的根目录，这里优先插入
        List<FileObject> list = dbFileObjects.parallelStream().filter(v -> v.getIsDir() &&
                Objects.equals(0L, v.getIsDelete()) &&
                v.getTempUid() != null
        ).toList();
        list.forEach(v -> v.setId(null));
        folderCounter.addAndGet(list.size());
        //批量插入数据
        transactionTemplate.execute(status -> {
            MybatisBatch.Method<FileObject> mapperMethod = new MybatisBatch.Method<>(FileObjectMapper.class);
            return MybatisBatchUtils.execute(sqlSessionFactory, list, mapperMethod.insert());
        });
        //标记已经完成插入获取有ID的这一批数据设置updateDate不为空
        list.forEach(v -> v.setUpdateTime(LocalDateTime.now()));
        //2. 然后查出有关联之前最先一开始关联UID的数据-根据字段prentID之后修改真正的parentID进行关联
        List<Long> tempIds = list.stream().map(FileObject::getTempUid).toList();
        List<FileObject> list2 = dbFileObjects.stream().filter(v -> v.getIsDir() &&
                Objects.equals(0L, v.getIsDelete()) &&
                tempIds.contains(v.getParentId())
        ).peek(v -> list.stream().filter(v1 -> v1.getTempUid().equals(v.getParentId()))
                .findFirst().ifPresent(vvv -> v.setParentId(vvv.getId()))).toList();
        //批量插入数据
        transactionTemplate.execute(status -> {
            //再一次细分List2分为更新和新增数据
            List<FileObject> insert = list2.stream().filter(v -> v.getUpdateTime() == null)
                    .peek(iv -> iv.setId(null)).toList();
            List<FileObject> update = list2.stream().filter(v -> v.getUpdateTime() != null).toList();
            List<BatchResult> results = new ArrayList<>();
            MybatisBatch.Method<FileObject> mapperMethod = new MybatisBatch.Method<>(FileObjectMapper.class);
            if (!insert.isEmpty()) {
                folderCounter.addAndGet(insert.size());
                results.addAll(MybatisBatchUtils.execute(sqlSessionFactory, insert, mapperMethod.insert()));
            }
            if (!update.isEmpty()) {
                results.addAll(MybatisBatchUtils.execute(sqlSessionFactory, update, mapperMethod.updateById()));
            }
            return results;
        });

        //END----------------------------------

        //扫描只是文件类型的数据
        updateLopFile(files, dbFileObjects, rootObject, false);
        //更新数组内的逻辑
        delIds.clear();
        delIds.addAll(dbFileObjects.parallelStream()
                .filter(v -> v.getIsDelete() != 0 && v.getId() != null && !v.getFilePath().equals(FileNativeService.DELIMITER))
                .map(FileObject::getId)
                .toList());
        if (!delIds.isEmpty()) {
            fileObjectMapper.deleteBatchIds(delIds);

        }

        List<FileObject> saveList = dbFileObjects.parallelStream().filter(v -> v.getId() == null && v.getIsDelete() == 0)
                .toList();
        if (!saveList.isEmpty()) {
            transactionTemplate.execute(status -> {
                MybatisBatch.Method<FileObject> mapperMethod = new MybatisBatch.Method<>(FileObjectMapper.class);
                return MybatisBatchUtils.execute(sqlSessionFactory, saveList, mapperMethod.insert());
            });
        }
        log.info("===========>>>>> Bucket：{} 新增目录:{}个, 新增文件{}个", buckets.getBucketsName(), folderCounter.get(), saveList.size());
        log.info("===========>>>>> Bucket：{} 删除{}个", buckets.getBucketsName(), delIds.size());
    }

    /**
     * 文件扫描逻辑
     *
     * @param files
     * @param dbFileObjects
     * @param root
     */
    private void updateLopFile(File[] files, List<FileObject> dbFileObjects, FileObject root, boolean actionDir) {
        if (actionDir) {
            for (File f : files) {
                if (f.isDirectory()) {
                    //目录
                    //判断目录是否存在
                    Optional<FileObject> search = dbFileObjects.parallelStream()
                            .filter(v -> Objects.equals(0L, v.getIsDelete()) &&
                                    root.getId().equals(v.getParentId()) &&
                                    v.getIsDir() &&
                                    v.getRealPath().equals(f.getAbsolutePath()))
                            .findAny();
                    if (search.isPresent()) {
                        //目录存在
                        File[] dirFiles = f.listFiles();
                        if (dirFiles == null || dirFiles.length == 0) {
                            //如果这个目录下没有任何的文件
                            //删除这个目录下所有的子文件但是不包含自身
                            for (FileObject object : dbFileObjects) {
                                if (object.getRealPath().startsWith(f.getAbsolutePath())
                                        && !object.getRealPath().equals(f.getAbsolutePath())) {
                                    object.setIsDelete(object.getId());
                                }
                            }
                        } else {
                            //否则扫描此文件夹下的文件
                            updateLopFile(dirFiles, dbFileObjects, search.get(), true);
                        }
                    } else {
                        //不存在的目录
                        FileObject insertObject = createFileObjectBase(f, root);
                        insertObject.setId(IdUtil.getSnowflakeNextId());
                        insertObject.setTempUid(insertObject.getId());
                        dbFileObjects.add(insertObject);
                        File[] dirFiles = f.listFiles();
                        if (dirFiles != null && dirFiles.length != 0) {
                            updateLopFile(dirFiles, dbFileObjects, insertObject, true);
                        }
                    }
                }
            }
        } else {
            for (File f : files) {
                if (!f.isDirectory()) {
                    //是个文件
                    Optional<FileObject> search = dbFileObjects.parallelStream()
                            .filter(v -> Objects.equals(0L, v.getIsDelete()) &&
                                    v.getParentId().equals(root.getId()) &&
                                    !v.getIsDir() &&
                                    v.getRealPath().equals(f.getAbsolutePath()))
                            .findAny();
                    if (search.isEmpty()) {
                        //不存在创建文件
                        FileObject insertFile = createFileObjectBase(f, root);
                        insertFile.setIsDir(false);
                        insertFile.setFileSize(FileUtil.size(f, true));
                        insertFile.setFileMd5(SecureUtil.md5(f));
                        try {
                            insertFile.setFileContentType(tika.detect(f));
                        } catch (IOException e) {
                            log.error("获取文件ContentType错误", e);
                            insertFile.setFileContentType("application/octet-stream");
                        }
                        dbFileObjects.add(insertFile);
                    }
                } else {
                    //是个目录
                    //拿到目录的对应父对象
                    Optional<FileObject> search = dbFileObjects.parallelStream()
                            .filter(v -> Objects.equals(0L, v.getIsDelete()) &&
                                    v.getParentId().equals(root.getId()) &&
                                    v.getIsDir() &&
                                    v.getRealPath().equals(f.getAbsolutePath()))
                            .findAny();
                    if (search.isEmpty()) {
                        throw new RuntimeException(StrUtil.format("错误：找不到父文件对象：{}", f.getAbsolutePath()));
                    }
                    File[] dirFiles = f.listFiles();
                    if (dirFiles != null && dirFiles.length != 0) {
                        updateLopFile(dirFiles, dbFileObjects, search.get(), false);
                    }
                }
            }
        }
    }


    private FileObject createFileObjectBase(File file, FileObject parent) {
        Assert.notNull(parent.getId());
        FileObject fileObject = new FileObject();
        fileObject.setFileName(file.getName());
        fileObject.setParentId(parent.getId());
        fileObject.setBucketsId(parent.getBucketsId());
        fileObject.setFilePath(fileNativeService.getPath(parent.getFilePath(), file.getName()));
        fileObject.setRealPath(file.getAbsolutePath());
        fileObject.setIsDir(true);
        fileObject.setIsDelete(0L);
        fileObject.setCreateTime(LocalDateTime.now());
        return fileObject;
    }
}
