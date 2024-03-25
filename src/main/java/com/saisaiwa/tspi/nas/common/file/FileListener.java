package com.saisaiwa.tspi.nas.common.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.core.toolkit.MybatisBatchUtils;
import com.saisaiwa.tspi.nas.common.util.SpringUtils;
import com.saisaiwa.tspi.nas.domain.entity.Buckets;
import com.saisaiwa.tspi.nas.domain.entity.FileObject;
import com.saisaiwa.tspi.nas.domain.file.FObjectSearch;
import com.saisaiwa.tspi.nas.mapper.BucketsMapper;
import com.saisaiwa.tspi.nas.mapper.FileObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tika.Tika;
import org.slf4j.MDC;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description:
 * @date: 2024/03/12 16:02
 * @author: saisiawa
 **/
@Slf4j
@Getter
public class FileListener implements FileAlterationListener {

    /**
     * 事务提交最大单位数量
     */
    private static final int COMMIT_MAX_COUNT = 300;

    private static final int INIT_CNT = 2;

    private static final Tika TIKA = new Tika();

    private final Buckets buckets;

    private long initFlag;

    private final FileNativeService fileNativeService;

    private final FileObjectMapper fileObjectMapper;

    private final TransactionTemplate transactionTemplate;

    private final Map<String, String> parentMdcContext;

    private int currentSqlCount;

    private final SqlSessionFactory sqlSessionFactory;

    private final List<FileObject> insertList = new ArrayList<>();
    private final List<String> delList = new ArrayList<>();
    private final List<FileObject> dbDataList = new ArrayList<>();

    public FileListener(Buckets buckets) {
        parentMdcContext = MDC.getCopyOfContextMap();
        this.buckets = buckets;
        this.fileNativeService = SpringUtils.getBean(FileNativeService.class);
        this.fileObjectMapper = SpringUtils.getBean(FileObjectMapper.class);
        this.transactionTemplate = SpringUtils.getBean(TransactionTemplate.class);
        this.sqlSessionFactory = SpringUtils.getBean(SqlSessionFactory.class);
        FileLockUtil.addLock(buckets.getId());
    }

    @Override
    public void onDirectoryChange(File file) {
        if (initFlag < INIT_CNT) {
            return;
        }
        printFileInfo(file, "onDirectoryChange");
    }

    @Override
    public void onDirectoryCreate(File file) {
        if (initFlag < INIT_CNT) {
            return;
        }
        loadDbAllDirFileObject();
        if (checkIsExist(file)) {
            return;
        }
        printFileInfo(file, "文件夹新建");
        FileObject parentObject = getParentObjectAndDir(file.getParentFile());
        if (parentObject == null || !parentObject.getIsDir()) {
            log.error("文件夹新增失败，找不到父路径或者不为文件夹：{}", parentObject);
            return;
        }
        try {
            FileObject fileObject = new FileObject();
            fileObject.setFileName(file.getName());
            fileObject.setBucketsId(buckets.getId());
            fileObject.setParentId(parentObject.getId());
            fileObject.setTempUid(IdUtil.getSnowflakeNextId());
            fileObject.setId(fileObject.getTempUid());
            fileNativeService.createFolderFileObject(fileObject, parentObject, true);
            insertList.add(fileObject);
            commit();
        } catch (Exception e) {
            log.error("onDirectoryCreate Error", e);
        }
    }

    @Override
    public void onDirectoryDelete(File file) {
        if (initFlag < INIT_CNT) {
            return;
        }
        try {
            printFileInfo(file, "文件夹删除");
            delList.add(file.getAbsolutePath());
            commit();
        } catch (Exception e) {
            log.error("onDirectoryDelete ", e);
        }
    }

    @Override
    public void onFileChange(File file) {
        if (initFlag < INIT_CNT) {
            return;
        }
        printFileInfo(file, "onFileChange");
    }

    @Override
    public void onFileCreate(File file) {
        if (initFlag < INIT_CNT) {
            return;
        }
        if (file.isDirectory()) {
            return;
        }
        loadDbAllDirFileObject();
        if (checkIsExist(file)) {
            return;
        }
        try {
            FileObject parentObject = getParentObjectAndDir(file.getParentFile());
            if (parentObject == null || !parentObject.getIsDir()) {
                log.error("文件新增失败，找不到父路径或者不为文件夹：{}", parentObject);
                return;
            }
            loadParentChildFile(parentObject);
            String filepath = fileNativeService.getPath(parentObject.getFilePath(), file.getName());
            if (checkIsExist(file)) {
                return;
            }
            printFileInfo(file, "文件创建");
            FileObject fileObject = new FileObject();
            String md5 = SecureUtil.md5(file);
            fileObject.setBucketsId(buckets.getId());
            fileObject.setFileName(file.getName());
            fileObject.setFilePath(filepath);
            fileObject.setRealPath(file.getAbsolutePath());
            fileObject.setFileMd5(md5);
            fileObject.setFileSize(FileUtil.size(file, true));
            fileObject.setParentId(parentObject.getId());
            fileObject.setCreateTime(LocalDateTime.now());
            fileObject.setIsDir(false);
            try {
                fileObject.setFileContentType(TIKA.detect(file));
            } catch (IOException e) {
                log.error("获取文件ContentType错误", e);
                fileObject.setFileContentType("application/octet-stream");
            }
            insertList.add(fileObject);
            commit();
        } catch (Exception e) {
            log.error("onFileCreate ", e);
        }
    }

    @Override
    public void onFileDelete(File file) {
        if (initFlag < INIT_CNT) {
            return;
        }
        try {
            printFileInfo(file, "文件删除");
            delList.add(file.getAbsolutePath());
            commit();
        } catch (Exception e) {
            log.error("onFileDelete", e);
        }
    }

    @Override
    public void onStart(FileAlterationObserver fileAlterationObserver) {
        MDC.setContextMap(parentMdcContext);
        if (!fileAlterationObserver.getDirectory().exists() || !fileAlterationObserver.getDirectory().isDirectory()) {
            log.info("监听的目录被删除了");
            //移除自身
            SpringUtils.getBean(FileLocalScanService.class)
                    .removeListener(this.buckets);
            SpringUtils.getBean(BucketsMapper.class)
                    .deleteById(buckets.getId());
            FileLockUtil.removeLock(this.buckets.getId());
            initFlag = Long.MIN_VALUE;
            return;
        }
        if (initFlag > INIT_CNT) {
            return;
        }
        initFlag++;
        currentSqlCount = 0;
    }

    @Override
    public void onStop(FileAlterationObserver fileAlterationObserver) {
        currentSqlCount = COMMIT_MAX_COUNT * 2;
        commit();
    }


    private synchronized void commit() {
        currentSqlCount++;
        if (currentSqlCount < COMMIT_MAX_COUNT) {
            return;
        }
        currentSqlCount = 0;
        ReentrantLock reentrantLock = FileLockUtil.getLock(this.buckets.getId());
        boolean lockState = false;
        //批量
        try {
            if (!delList.isEmpty() || !insertList.isEmpty()) {
                reentrantLock.lock();
                lockState = true;
            }
            if (!insertList.isEmpty()) {
                MybatisBatch.Method<FileObject> mapperMethod = new MybatisBatch.Method<>(FileObjectMapper.class);
                //获取到了锁查询查询要插入的值数据库内是有否有相同的进行去重操作
                List<FileObject> checkedList = fileObjectMapper.checkRealPathTheDuplicateValue(this.buckets.getId(),
                        insertList.stream().map(FileObject::getRealPath).toList());
                if (!checkedList.isEmpty()) {
                    for (FileObject f : checkedList) {
                        insertList.removeIf(v -> f.getRealPath().equals(v.getRealPath()));
                    }
                }
                if (!insertList.isEmpty()) {
                    List<FileObject> dirInserts = insertList.stream().filter(v -> v.getTempUid() != null && v.getIsDir())
                            .peek(vv -> vv.setId(null))
                            .toList();

                    transactionTemplate.execute(status -> MybatisBatchUtils.execute(sqlSessionFactory, dirInserts, mapperMethod.insert()));

                    insertList.forEach(v -> dirInserts.stream().filter(vv -> vv.getTempUid().equals(v.getParentId()))
                            .findAny().ifPresent(r -> {
                                v.setParentId(r.getId());
                                v.setUpdateTime(LocalDateTime.now());
                            }));
                    List<FileObject> updates = insertList.stream().filter(v -> v.getIsDir() && v.getUpdateTime() != null).toList();
                    List<FileObject> insert = insertList.stream().filter(v -> !v.getIsDir()).toList();
                    if (!updates.isEmpty()) {
                        transactionTemplate.execute(status -> MybatisBatchUtils.execute(sqlSessionFactory, updates, mapperMethod.updateById()));
                    }

                    if (!insert.isEmpty()) {
                        transactionTemplate.execute(status -> MybatisBatchUtils.execute(sqlSessionFactory, insert, mapperMethod.insert()));
                    }
                }
            }
            if (!delList.isEmpty()) {
                log.info("监听删除: {}个", delList.size());
                fileObjectMapper.deleteAllByRealPathAndStartWithBatch(delList);
            }
        } catch (Exception e) {
            log.error("批量操作出错：", e);
        } finally {
            if (lockState && reentrantLock.isLocked()) {
                reentrantLock.unlock();
            }
        }
        insertList.clear();
        delList.clear();
        dbDataList.clear();
    }

    /**
     * 懒加载查询数据库dir类型的数据
     */
    private synchronized void loadDbAllDirFileObject() {
        if (dbDataList.isEmpty()) {
            dbDataList.addAll(fileObjectMapper.getDirAllListByBucketId(this.buckets.getId()));
        }
    }

    /**
     * 查询子文件列表数据
     * 入参为父目录，查询当前数组内是否有此目录对应的文件对象
     * 如果有找到则判断是否被加载过，依据是数组内是否有父ID为此对象的。
     *
     * @param dirFile
     */
    public void loadParentChildFile(FileObject dirFile) {
//        dbDataList.stream().filter(v -> v.getIsDir() &&
//                        v.getRealPath().equals(parent.getAbsolutePath()))
//                .findAny()
//                .ifPresent(vv -> {
////                    //在找一下是否有parentID有此文件的
////                    if (dbDataList.stream().anyMatch(k -> k.getParentId() != null && k.getParentId().equals(vv.getId()))) {
////                        return;
////                    }
//                    FObjectSearch search = new FObjectSearch();
//                    search.setBucketId(this.buckets.getId());
//                    search.setParentId(vv.getId());
//                    search.setCurrent(1);
//                    search.setSize(-1);
//                    dbDataList.addAll(fileObjectMapper.searchFileObject(search));
//                });

        if (dbDataList.stream().noneMatch(v -> v.getParentId() != null && v.getParentId().equals(dirFile.getId()))) {
            FObjectSearch search = new FObjectSearch();
            search.setBucketId(this.buckets.getId());
            search.setParentId(dirFile.getId());
            search.setCurrent(1);
            search.setSize(-1);
            dbDataList.addAll(fileObjectMapper.searchFileObject(search));
        }
    }


    /**
     * 根据File文件的决定路径查询数据库或者是insert操作内的数据查询此对象
     *
     * @param f
     * @return
     */
    private FileObject getParentObjectAndDir(File f) {
        String absolutePath = f.getAbsolutePath();
        FileObject fileObject = dbDataList.stream().filter(v -> v.getRealPath().equals(absolutePath) && v.getIsDir())
                .findFirst().orElse(null);
        if (fileObject == null) {
            fileObject = insertList.stream().filter(v -> v.getIsDir() && v.getRealPath().equals(absolutePath))
                    .findFirst().orElse(null);
        }
        return fileObject;
    }

    /**
     * 检查是否存在此路径的文件对象
     *
     * @param f
     * @return
     */
    private boolean checkIsExist(File f) {
        String absolutePath = f.getAbsolutePath();
        return dbDataList.stream().anyMatch(v -> v.getRealPath().equals(absolutePath));
    }


    private void printFileInfo(File file, String msg) {
        if (initFlag >= 2) {
            log.info("{} FileInfo:Name:{} Size:{} Path:{}", msg, file.getName(), FileUtil.size(file, true), file.getAbsolutePath());
        }
    }
}
