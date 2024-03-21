package com.saisaiwa.tspi.nas.common.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import com.saisaiwa.tspi.nas.common.util.SpringUtils;
import com.saisaiwa.tspi.nas.domain.entity.Buckets;
import com.saisaiwa.tspi.nas.domain.entity.FileObject;
import com.saisaiwa.tspi.nas.mapper.BucketsMapper;
import com.saisaiwa.tspi.nas.mapper.FileObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.tika.Tika;
import org.slf4j.MDC;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
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
    private static final int COMMIT_MAX_COUNT = 100;

    private static final int INIT_CNT = 2;

    private static final Tika TIKA = new Tika();

    private final Buckets buckets;

    private long initFlag;

    private final FileNativeService fileNativeService;

    private final FileObjectMapper fileObjectMapper;

    private final PlatformTransactionManager platformTransactionManager;

    private final TransactionDefinition transactionDefinition;

    private TransactionStatus transactionStatus;

    private final Map<String, String> parentMdcContext;

    private final ReentrantLock lock;

    private int currentSqlCount;


    public FileListener(Buckets buckets) {
        parentMdcContext = MDC.getCopyOfContextMap();
        this.buckets = buckets;
        lock = new ReentrantLock();
        this.fileNativeService = SpringUtils.getBean(FileNativeService.class);
        this.fileObjectMapper = SpringUtils.getBean(FileObjectMapper.class);
        this.platformTransactionManager = SpringUtils.getBean(PlatformTransactionManager.class);
        this.transactionDefinition = SpringUtils.getBean(TransactionDefinition.class);
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
        //判断是否存在相同的文件
        if (fileObjectMapper.getByRealPathAndBucketsId(file.getAbsolutePath(), buckets.getId()) != null) {
            return;
        }
        printFileInfo(file, "文件夹新建");
        String parentRealPath = file.getParentFile().getAbsolutePath();
        FileObject parentObject = fileObjectMapper.getByRealPathAndBucketsId(parentRealPath, buckets.getId());
        if (parentObject == null || !parentObject.getIsDir()) {
            log.error("文件夹新增失败，找不到父路径或者不为文件夹：{}", parentObject);
            return;
        }
        try {
            FileObject fileObject = new FileObject();
            fileObject.setFileName(file.getName());
            fileObject.setBucketsId(buckets.getId());
            fileObject.setParentId(parentObject.getId());
            fileNativeService.createFolderFileObject(fileObject, parentObject, true);
            fileObjectMapper.insert(fileObject);
            sqlAutoCommitAndStart();
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
            int delCount = fileObjectMapper.deleteAllByRealPathAndStartWith(file.getAbsolutePath());
            log.info("删除文件夹数量：{}", delCount);
            sqlAutoCommitAndStart();
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
        //判断是否存在相同的文件
        if (fileObjectMapper.getByRealPathAndBucketsId(file.getAbsolutePath(), buckets.getId()) != null) {
            return;
        }
        try {
            printFileInfo(file, "文件创建");
            String parentRealPath = file.getParentFile().getAbsolutePath();
            FileObject parentObject = fileObjectMapper.getByRealPathAndBucketsId(parentRealPath, buckets.getId());
            if (parentObject == null || !parentObject.getIsDir()) {
                log.error("文件新增失败，找不到父路径或者不为文件夹：{}", parentObject);
                return;
            }
            FileObject fileObject = new FileObject();
            String md5 = SecureUtil.md5(file);
            fileObject.setBucketsId(buckets.getId());
            fileObject.setFileName(file.getName());
            fileObject.setFilePath(fileNativeService.getPath(parentObject.getFilePath(), fileObject.getFileName()));
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
            fileObjectMapper.insert(fileObject);
            sqlAutoCommitAndStart();
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
            int delCount = fileObjectMapper.deleteAllByRealPathAndStartWith(file.getAbsolutePath());
            log.info("删除文件-数量：{}", delCount);
            sqlAutoCommitAndStart();
        } catch (Exception e) {
            log.error("onFileDelete", e);
        }
    }

    @Override
    public void onStart(FileAlterationObserver fileAlterationObserver) {
        lock.lock();
        MDC.setContextMap(parentMdcContext);
        if (!fileAlterationObserver.getDirectory().exists() || !fileAlterationObserver.getDirectory().isDirectory()) {
            log.info("监听的目录被删除了");
            //移除自身
            SpringUtils.getBean(FileLocalScanService.class)
                    .removeListener(this.buckets);
            SpringUtils.getBean(BucketsMapper.class)
                    .deleteById(buckets.getId());
            return;
        }
        if (initFlag > INIT_CNT) {
            startTransaction();
            return;
        }
        initFlag++;
    }

    @Override
    public void onStop(FileAlterationObserver fileAlterationObserver) {
        commit();
        if (lock.isLocked()) {
            lock.unlock();
        }
    }

    private void sqlAutoCommitAndStart() {
        currentSqlCount++;
        if (currentSqlCount > COMMIT_MAX_COUNT) {
            commit();
            startTransaction();
        }
    }


    private void commit() {
        if (transactionStatus != null) {
            platformTransactionManager.commit(transactionStatus);
            transactionStatus = null;
        }
    }

    private void startTransaction() {
        if (transactionStatus == null) {
            transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
            currentSqlCount = 0;
        }
    }


    private void printFileInfo(File file, String msg) {
        if (initFlag >= 2) {
            log.info("{} FileInfo:Name:{} Size:{} Path:{}", msg, file.getName(), FileUtil.size(file, true), file.getAbsolutePath());
        }
    }
}
