package com.saisaiwa.tspi.nas.common.file;

import cn.hutool.core.io.FastByteBuffer;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.batch.MybatisBatch;
import com.baomidou.mybatisplus.core.toolkit.MybatisBatchUtils;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.domain.entity.Buckets;
import com.saisaiwa.tspi.nas.domain.entity.FileBlockRecords;
import com.saisaiwa.tspi.nas.domain.entity.FileObject;
import com.saisaiwa.tspi.nas.mapper.FileBlockRecordsMapper;
import com.saisaiwa.tspi.nas.mapper.FileObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * @description:
 * @date: 2024/03/12 10:42
 * @author: saisiawa
 **/
@Component
@Slf4j
public class FileNativeService {

    @Resource
    private FileObjectMapper fileObjectMapper;

    @Resource
    private FileBlockRecordsMapper fileBlockRecordsMapper;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    private final Tika tika = new Tika();

    public static final String DELIMITER = "/";

    /**
     * 创建存储桶
     *
     * @param buckets
     * @return
     */
    public void createBuckets(Buckets buckets) {
        if (buckets.getId() == null) {
            throw new BizException(RespCode.FILE_ERROR);
        }
        String point = buckets.getMountPoint();
        if (FileUtil.exist(point)) {
            log.error("创建存储桶失败，已经存在该路径的文件");
            throw new BizException(RespCode.FILE_ERROR);
        }
        File mkdir = FileUtil.mkdir(point);
        if (mkdir == null) {
            log.error("创建存储桶失败");
            throw new BizException(RespCode.FILE_ERROR);
        }
        if (fileObjectMapper.getRootObject(buckets.getId()) != null) {
            throw new BizException(RespCode.FILE_ERROR);
        }
        FileObject root = new FileObject();
        root.setBucketsId(buckets.getId());
        root.setParentId(null);
        root.setFileName(DELIMITER);
        root.setIsDir(true);
        root.setFilePath(DELIMITER);
        root.setRealPath(mkdir.getAbsolutePath());
        fileObjectMapper.insert(root);
    }


    /**
     * 删除一个存储桶，递归删除所有
     *
     * @param buckets
     * @return
     */
    public boolean deleteBuckets(Buckets buckets) {
        String point = buckets.getMountPoint();
        if (FileUtil.exist(point)) {
            return FileUtil.del(point);
        }
        FileLockUtil.removeLock(buckets.getId());
        return true;
    }


    /**
     * 检查是否存在此文件或目录
     *
     * @param point
     * @return
     */
    public boolean has(String point) {
        return FileUtil.exist(point);
    }


    /**
     * 返回文件占用总大小，bytes长度
     *
     * @param point 指向
     * @return long
     */
    public long getSize(String point) {
        if (!FileUtil.exist(point)) {
            return 0L;
        }
        return FileUtil.size(new File(point), true);
    }

    /**
     * 获取文件缓存数组缓冲区
     *
     * @param fileObject 文件对象
     * @return {@link FastByteBuffer}
     */
    public FastByteBuffer getFileCacheArrayBuffer(FileObject fileObject) {
        String filePath = fileObject.getRealPath();
        if (!FileUtil.exist(filePath)) {
            return null;
        }
        byte[] bytes = FileUtil.readBytes(filePath);
        FastByteBuffer fastByteBuffer = new FastByteBuffer(bytes.length);
        fastByteBuffer.append(bytes);
        return fastByteBuffer;
    }


    /**
     * 输出文件流
     *
     * @param fileObject 文件对象
     */
    public FileGetNativeInfo writeOutputFileStream(FileObject fileObject, String range) {
        File file = new File(fileObject.getRealPath());
        long start = 0;
        long end = file.length() - 1;

        if (range != null && range.startsWith("bytes=")) {
            String[] rangeValues = range.substring(6).split("-");
            start = Long.parseLong(rangeValues[0]);
            if (rangeValues.length > 1 && !rangeValues[1].isEmpty()) {
                end = Long.parseLong(rangeValues[1]);
            }
        }

        try {
            FileRangeInputStream stream = new FileRangeInputStream(file, start, end);
            return new FileGetNativeInfo(stream, (end - start + 1), start, end, file.length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 新建文件夹
     *
     * @param fileObject
     * @param targetObject
     * @param isOverwrite
     * @return
     */
    public boolean createFolderFileObject(FileObject fileObject, FileObject targetObject, boolean isOverwrite) {
        if (!targetObject.getIsDir()) {
            return false;
        }
        File tf = new File(targetObject.getRealPath());
        if (!tf.exists()) {
            return false;
        }
        File file = new File(tf, fileObject.getFileName());
        if (file.exists() && !isOverwrite) {
            return false;
        }
        fileObject.setRealPath(file.getAbsolutePath());
        fileObject.setFilePath(getPath(targetObject.getFilePath(), fileObject.getFileName()));
        fileObject.setIsDir(true);
        fileObject.setParentId(targetObject.getId());
        return file.mkdir();
    }

    /**
     * 写入文件
     *
     * @param file          文件
     * @param fileObject    文件对象
     * @param targetFolder  要写入的目标文件夹
     * @param autoOverwrite 自动覆盖
     */
    public void uploadFileObject(MultipartFile file, FileObject fileObject, FileObject targetFolder, boolean autoOverwrite) {
        if (!(targetFolder.getIsDir() && !fileObject.getIsDir())) {
            throw new BizException(RespCode.FILE_ERROR);
        }
        if (!FileUtil.exist(targetFolder.getRealPath())) {
            throw new BizException(RespCode.FILE_ERROR);
        }
        File parentFile = new File(targetFolder.getRealPath());
        if (!parentFile.isDirectory()) {
            throw new BizException(RespCode.FILE_ERROR);
        }
        File target = new File(parentFile, fileObject.getFileName());

        if (FileUtil.exist(target) && autoOverwrite) {
            if (FileUtil.isDirectory(target)) {
                log.error("写入文件失败,无法覆盖此文件，因为此文件是文件夹！");
                throw new BizException(RespCode.FILE_ERROR);
            }
            FileUtil.del(target);
            //在删除掉原先的数据库记录
            fileObjectMapper.deleteByRealPathAndBucketsId(targetFolder.getBucketsId(), List.of(target.getAbsolutePath()));
        }
        try {
            file.transferTo(target);
        } catch (IOException e) {
            log.error("文件写入失败：", e);
            throw new BizException(RespCode.FILE_ERROR);
        }
        if (!FileUtil.exist(target)) {
            log.error("文件写入失败为NULL");
            throw new BizException(RespCode.FILE_ERROR);
        }
        String md5 = SecureUtil.md5(target);
        fileObject.setFilePath(getPath(targetFolder.getFilePath(), fileObject.getFileName()));
        fileObject.setRealPath(target.getAbsolutePath());
        fileObject.setFileMd5(md5);
        fileObject.setFileSize(FileUtil.size(target, true));
        fileObject.setParentId(targetFolder.getId());
        fileObject.setCreateTime(LocalDateTime.now());
        fileObject.setCreateUser(SessionInfo.getAndNull().getUid());
        fileObject.setBucketsId(targetFolder.getBucketsId());
        try {
            fileObject.setFileContentType(tika.detect(target));
        } catch (IOException e) {
            log.error("获取文件ContentType错误", e);
            fileObject.setFileContentType("application/octet-stream");
        }

    }


    /**
     * 删除文件对象
     *
     * @param fileObject 文件对象
     * @return boolean
     */
    public boolean deleteFileObject(FileObject fileObject) {
        if (fileObject.getFilePath().equals(DELIMITER)) {
            log.error("deleteFileObject 根目录无法删除");
            throw new BizException(RespCode.FILE_ERROR);
        }
        if (!FileUtil.exist(fileObject.getRealPath())) {
            return true;
        }
        return FileUtil.del(fileObject.getRealPath());
    }


    /**
     * 移动文件
     *
     * @param sourceFileObject 源文件
     * @param targetFolder     目标文件夹
     * @param autoOverwrite    是否自动覆盖
     * @return boolean
     */
    public boolean moveFileObject(FileObject sourceFileObject, FileObject targetFolder, boolean autoOverwrite) {
        if (!FileUtil.exist(sourceFileObject.getRealPath()) || !FileUtil.exist(targetFolder.getRealPath())) {
            return false;
        }
        File source = new File(sourceFileObject.getRealPath());
        File target = new File(targetFolder.getRealPath());
        if (!target.isDirectory()) {
            return false;
        }
        //查询原始此路径下所有的文件
        List<FileObject> list = fileObjectMapper.selectAllByStartFilePath(sourceFileObject.getFilePath());

        FileObject root = list.stream().filter(f -> f.getId().equals(sourceFileObject.getId())).findAny().orElseThrow();
        root.setParentId(targetFolder.getId());
        root.setFilePath(getPath(targetFolder.getFilePath(), root.getFileName()));
        root.setRealPath(targetFolder.getRealPath() + File.separator + root.getFileName());

        Set<String> delRealPaths = new HashSet<>();
        for (FileObject object : list) {
            object.setBucketsId(targetFolder.getBucketsId());
            object.setCreateUser(SessionInfo.getAndNull().getUid());
            object.setCreateTime(LocalDateTime.now());
            object.setUpdateTime(LocalDateTime.now());
            object.setUpdateUser(null);
            if (!object.getId().equals(sourceFileObject.getId())) {
                String path = object.getFilePath();
                path = root.getFilePath() + path.substring(sourceFileObject.getFilePath().length());
                object.setFilePath(path);

                path = object.getRealPath();
                path = root.getRealPath() + path.substring(sourceFileObject.getRealPath().length());
                object.setRealPath(path);
            }

            if (FileUtil.exist(object.getRealPath())) {
                if (autoOverwrite) {
                    delRealPaths.add(object.getRealPath());
                } else {
                    if (object.getId().equals(sourceFileObject.getId())) {
                        return false;
                    }
                    object.setRealPath(null);
                }
            }

        }
        if (!delRealPaths.isEmpty()) {
            fileObjectMapper.deleteByRealPathAndBucketsId(targetFolder.getBucketsId(), delRealPaths);
        }

        list.removeIf(v -> StrUtil.isBlank(v.getRealPath()));
        //执行批量操作
        transactionTemplate.execute((status -> {
            MybatisBatch.Method<FileObject> mapperMethod = new MybatisBatch.Method<>(FileObjectMapper.class);
            return MybatisBatchUtils.execute(sqlSessionFactory, list, mapperMethod.updateById());
        }));

        FileUtil.move(source, target, autoOverwrite);

        return true;
    }


    /**
     * 复制文件对象
     *
     * @param source       来源
     * @param targetFolder 目标文件夹
     * @param isOverwrite  是否覆盖
     * @return {@link FileObject}
     */
    public boolean copyFileObject(FileObject source, FileObject targetFolder, boolean isOverwrite) {
        if (!targetFolder.getIsDir()) {
            return false;
        }
        if (!FileUtil.exist(source.getRealPath()) || !FileUtil.exist(targetFolder.getRealPath())) {
            return false;
        }
        File src = new File(source.getRealPath());
        File target = new File(targetFolder.getRealPath());
        if (!FileUtil.isDirectory(target)) {
            return false;
        }

        Set<String> delRulePaths = new HashSet<>();
        List<FileObject> fileList = fileObjectMapper.selectAllByStartFilePath(source.getFilePath());

        FileObject root = fileList.stream().filter(f -> f.getId().equals(source.getId())).findAny().orElseThrow();
        root.setParentId(targetFolder.getId());
        root.setFilePath(getPath(targetFolder.getFilePath(), root.getFileName()));
        root.setRealPath(targetFolder.getRealPath() + File.separator + root.getFileName());

        for (FileObject object : fileList) {
            object.setBucketsId(targetFolder.getBucketsId());
            object.setCreateTime(LocalDateTime.now());
            object.setUpdateTime(null);
            object.setCreateUser(SessionInfo.getAndNull().getUid());
            if (object.getIsDir()) {
                object.setTempUid(object.getId());
            }

            String realPath = object.getRealPath();
            if (!object.getId().equals(source.getId())) {
                String filePath = object.getFilePath();
                filePath = root.getFilePath() + filePath.substring(source.getFilePath().length());
                object.setFilePath(filePath);
                realPath = root.getRealPath() + realPath.substring(source.getRealPath().length());
            }

            if (FileUtil.exist(realPath)) {
                if (isOverwrite) {
                    //如果覆盖
                    delRulePaths.add(realPath);
                } else {
                    //不覆盖
                    if (object.getId().equals(source.getId())) {
                        return false;
                    }
                    realPath = null;
                }
            }
            object.setRealPath(realPath);
        }
        if (!delRulePaths.isEmpty()) {
            fileObjectMapper.deleteByRealPathAndBucketsId(targetFolder.getBucketsId(), delRulePaths);
        }
        fileList.removeIf(v -> StrUtil.isBlank(v.getRealPath()));
        if (fileList.isEmpty()) {
            return false;
        }

        List<FileObject> insert = fileList.parallelStream().filter(FileObject::getIsDir).peek(v -> v.setId(null)).toList();
        if (!insert.isEmpty()) {
            transactionTemplate.execute((status -> {
                MybatisBatch.Method<FileObject> mapperMethod = new MybatisBatch.Method<>(FileObjectMapper.class);
                return MybatisBatchUtils.execute(sqlSessionFactory, insert, mapperMethod.insert());
            }));
        }

        fileList.parallelStream().forEach(v -> insert.stream().filter(f -> f.getTempUid().equals(v.getParentId()))
                .findAny().ifPresent(vv -> {
                    v.setParentId(vv.getId());
                    if (v.getIsDir()) {
                        v.setUpdateTime(LocalDateTime.now());
                    }
                }));
        List<FileObject> update = fileList.parallelStream().filter(f -> f.getIsDir() && f.getUpdateTime() != null).toList();
        if (!update.isEmpty()) {
            transactionTemplate.execute((status -> {
                MybatisBatch.Method<FileObject> mapperMethod = new MybatisBatch.Method<>(FileObjectMapper.class);
                return MybatisBatchUtils.execute(sqlSessionFactory, update, mapperMethod.updateById());
            }));
        }

        List<FileObject> insert2 = fileList.parallelStream().filter(f -> !f.getIsDir()).peek(v -> v.setId(null)).toList();
        if (!insert2.isEmpty()) {
            transactionTemplate.execute((status -> {
                MybatisBatch.Method<FileObject> mapperMethod = new MybatisBatch.Method<>(FileObjectMapper.class);
                return MybatisBatchUtils.execute(sqlSessionFactory, insert2, mapperMethod.insert());
            }));
        }

        FileUtil.copy(src, target, isOverwrite);
        return true;
    }


    /**
     * 文件重命名
     *
     * @param fileObject
     * @param newName
     * @return
     */
    public boolean renameFile(FileObject fileObject, String newName) {
        File file = new File(fileObject.getRealPath());
        if (!file.exists()) {
            return false;
        }
        if (file.getName().equals(newName)) {
            return false;
        }
        if (new File(file.getParentFile(), newName).exists()) {
            return false;
        }

        String filePath = fileObject.getFilePath();
        filePath = filePath.substring(0, filePath.lastIndexOf(DELIMITER) + 1) + file.getName();
        String realPath = fileObject.getRealPath();

        List<FileObject> update = new ArrayList<>();
        if (fileObject.getIsDir()) {
            // 是目录
            // 查询子文件列表
            List<FileObject> list = fileObjectMapper.selectAllByStartFilePath(fileObject.getFilePath());
            for (FileObject object : list) {
                if (object.getId().equals(fileObject.getId())) {
                    continue;
                }
                String itemFilePath = object.getFilePath();
                String itemRealPath = object.getRealPath();
                itemFilePath = filePath + itemFilePath.substring(fileObject.getFilePath().length());
                itemRealPath = realPath + itemRealPath.substring(fileObject.getRealPath().length());
                object.setFilePath(itemFilePath);
                object.setRealPath(itemRealPath);
                update.add(object);
            }
        }

        fileObject.setFileName(newName);
        fileObject.setFilePath(filePath);
        fileObject.setRealPath(new File(file.getParent(), newName).getAbsolutePath());
        fileObject.setUpdateUser(SessionInfo.getAndNull().getUid());
        fileObject.setUpdateTime(LocalDateTime.now());
        update.add(fileObject);
        //执行批量操作
        transactionTemplate.execute((status -> {
            MybatisBatch.Method<FileObject> mapperMethod = new MybatisBatch.Method<>(FileObjectMapper.class);
            return MybatisBatchUtils.execute(sqlSessionFactory, update, mapperMethod.updateById());
        }));
        FileUtil.rename(file, newName, true);
        return true;
    }


    /**
     * 初始化分块文件上传的临时目录
     *
     * @param records 记录
     */
    public void uploadBlockInit(FileBlockRecords records) {
        File tmpDir = FileUtil.getTmpDir();
        File file = new File(tmpDir, records.getFileMd5());
        FileUtil.del(file);
        FileUtil.mkdir(file);
        records.setDirPath(file.getAbsolutePath());
    }


    /**
     * 文件分块上传
     *
     * @param file     文件
     * @param fileName 文件名
     * @param dirPath  dir路径
     */
    public void uploadBlock(MultipartFile file, String fileName, String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new BizException(RespCode.FILE_ERROR);
        }
        File target = new File(dir, fileName);
        if (target.exists()) {
            log.error("uploadBlock error 文件已存在");
            throw new BizException(RespCode.FILE_ERROR);
        }
        try {
            file.transferTo(target);
        } catch (IOException e) {
            log.error("uploadBlock error", e);
            throw new BizException(RespCode.FILE_ERROR);
        }
        if (!target.exists()) {
            log.error("uploadBlock 文件不存在");
            throw new BizException(RespCode.FILE_ERROR);
        }
    }

    /**
     * 发起文件合并，并存储到指定的路径下
     *
     * @param records 记录
     * @return {@link FileObject}
     */
    public FileObject fileBlockMerge(FileBlockRecords records) {
        File dir = new File(records.getDirPath());
        if (!dir.exists() || !dir.isDirectory()) {
            throw new BizException(RespCode.FILE_ERROR);
        }
        FileObject parentFileObject = fileObjectMapper.selectById(records.getFileObjectParentId());
        Assert.notNull(parentFileObject);
        File target = new File(parentFileObject.getRealPath(), records.getFileName());
        if (target.exists() && !records.getIsOverwrite()) {
            throw new BizException(RespCode.FILE_ERROR, "该文件名已存在无法完成合并，请修改覆盖重试");
        }
        File tempTarget = new File(dir, records.getFileName());
        File[] dirList = dir.listFiles();
        if (dirList == null || dirList.length != records.getBlockCount()) {
            log.error("fileBlockMargin 物理文件数量不匹配");
            throw new BizException(RespCode.FILE_ERROR, "分块数量不足");
        }
        //排序文件
        List<File> filesList = Arrays.asList(dirList);
        //对分块文件排序
        filesList.sort((o1, o2) -> {
            String n1 = StrUtil.subBefore(o1.getName(), "-", false);
            String n2 = StrUtil.subBefore(o2.getName(), "-", false);
            return Integer.parseInt(n1) - Integer.parseInt(n2);
        });
        //向合并文件写的流
        try (RandomAccessFile accessFile = new RandomAccessFile(tempTarget, "rw")) {
            //缓存区
            byte[] bytes = new byte[1024];
            for (File file : filesList) {
                //读分块的流
                RandomAccessFile item = new RandomAccessFile(file, "r");
                int len;
                while ((len = item.read(bytes)) != -1) {
                    accessFile.write(bytes, 0, len);
                }
                item.close();
            }
            //MD5检查
            if (!SecureUtil.md5(tempTarget).equals(records.getFileMd5())) {
                log.error("fileBlockMerge 文件合并失败 MD5检查不通过");
                throw new BizException(RespCode.FILE_ERROR);
            }
            FileObject resultFileObject = new FileObject();
            File result = FileUtil.copy(tempTarget, new File(parentFileObject.getRealPath()), true);
            resultFileObject.setBucketsId(parentFileObject.getBucketsId());
            resultFileObject.setFileName(result.getName());
            resultFileObject.setFilePath(getPath(parentFileObject.getFilePath(), result.getName()));
            resultFileObject.setRealPath(result.getAbsolutePath());
            resultFileObject.setFileSize(FileUtil.size(result));
            resultFileObject.setFileMd5(records.getFileMd5());
            resultFileObject.setIsDir(false);
            resultFileObject.setParentId(parentFileObject.getId());
            resultFileObject.setCreateTime(LocalDateTime.now());
            resultFileObject.setCreateUser(records.getCreateUser());
            try {
                resultFileObject.setFileContentType(tika.detect(result));
            } catch (IOException e) {
                log.error("获取文件ContentType错误", e);
                resultFileObject.setFileContentType("application/octet-stream");
            }
            fileObjectMapper.insert(resultFileObject);
            return resultFileObject;
        } catch (Exception e) {
            log.error("fileBlockMerge 文件合并失败", e);
            throw new BizException(RespCode.FILE_ERROR);
        } finally {
            FileUtil.del(dir);
            //删除记录表
            fileBlockRecordsMapper.deleteById(records.getId());
        }
    }

    /**
     * 获取分段文件的已存在文件列表
     *
     * @param records 记录表
     * @return List<FileBlockEntity>
     */
    public List<FileBlockEntity> getFileBlockFiles(FileBlockRecords records) {
        File dir = new File(records.getDirPath());
        if (!dir.exists() || !dir.isDirectory()) {
            return List.of();
        }
        File[] dirList = dir.listFiles();
        if (dirList == null) {
            return List.of();
        }
        return Arrays.stream(dirList).map(v -> {
            FileBlockEntity block = new FileBlockEntity();
            block.setFileName(v.getName());
            block.setSize(FileUtil.size(v));
            String n1 = StrUtil.subBefore(v.getName(), "-", false);
            block.setNumber(Integer.parseInt(n1));
            return block;
        }).toList();
    }


    /**
     * 清理分块上传的临时目录文件
     *
     * @param records
     */
    public void cleanBlockTempFiles(FileBlockRecords records) {
        File dir = new File(records.getDirPath());
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        FileUtil.del(dir);
    }


    /**
     * 拼接路径
     *
     * @param parentPath
     * @param targetName
     * @return
     */
    public String getPath(String parentPath, String targetName) {
        if (parentPath.endsWith(DELIMITER)) {
            return parentPath.concat(targetName);
        }
        return parentPath.concat(DELIMITER).concat(targetName);
    }


    /**
     * 使用带锁的方法操作
     *
     * @param bucketId
     * @param call
     */
    public void lockAccept(Long bucketId, Consumer<FileNativeService> call) {
        ReentrantLock lock = FileLockUtil.getLock(bucketId);
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                try {
                    call.accept(this);
                } finally {
                    if (lock.isLocked()) {
                        lock.unlock();
                    }
                }
            } else {
                throw new BizException(RespCode.ERROR);
            }
        } catch (Exception e) {
            log.error("获取锁出现异常：", e);
            throw new BizException(RespCode.ERROR);
        }
    }

}
