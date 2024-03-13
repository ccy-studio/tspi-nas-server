package com.saisaiwa.tspi.nas.common.file;

import cn.hutool.core.io.FastByteBuffer;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
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
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            log.error("创建存储桶失败，已经存在改路径的文件");
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
     * @param fileObject   文件对象
     * @param outputStream 输出流
     * @param autoClose    自动关闭
     */
    public void writeOutputFileStream(FileObject fileObject, OutputStream outputStream, boolean autoClose) {
        String filePath = fileObject.getRealPath();
        if (!FileUtil.exist(filePath)) {
            if (autoClose) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            throw new BizException(RespCode.FILE_ERROR);
        }
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            IoUtil.copy(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            log.error("writeOutputFileStream Error", e);
            throw new BizException(RespCode.FILE_ERROR);
        } finally {
            IoUtil.close(inputStream);
            if (autoClose) {
                if (outputStream != null) {
                    IoUtil.close(outputStream);
                }
            }
        }
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
        fileObject.setFileContentType(FileUtil.extName(target));
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
            return false;
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
        if (!targetFolder.getIsDir()) {
            return false;
        }
        if (!FileUtil.exist(sourceFileObject.getRealPath()) || !FileUtil.exist(targetFolder.getRealPath())) {
            return false;
        }
        File source = new File(sourceFileObject.getRealPath());
        File target = new File(targetFolder.getRealPath());
        if (!target.isDirectory()) {
            return false;
        }
        //查询原始此路径下所有的文件
        List<FileObject> list = fileObjectMapper.selectAllByStartPath(sourceFileObject.getFilePath());
        Set<String> delRealPaths = new HashSet<>();
        for (FileObject object : list) {
            object.setBucketsId(targetFolder.getBucketsId());
            object.setCreateUser(SessionInfo.getAndNull().getUid());
            object.setCreateTime(LocalDateTime.now());
            object.setUpdateTime(LocalDateTime.now());
            object.setUpdateUser(null);
            String filePath = object.getFilePath();
            filePath = targetFolder.getFilePath() + filePath.substring(sourceFileObject.getFilePath().length());
            object.setFilePath(filePath);
            String realPath = object.getRealPath();
            realPath = targetFolder.getRealPath() + realPath.substring(sourceFileObject.getRealPath().length());
            if (FileUtil.exist(realPath)) {
                if (autoOverwrite) {
                    delRealPaths.add(realPath);
                } else {
                    realPath = null;
                }
            }
            object.setRealPath(realPath);
        }
        if (!delRealPaths.isEmpty()) {
            fileObjectMapper.deleteByRealPathAndBucketsId(targetFolder.getBucketsId(), delRealPaths);
        }

        try {
            FileUtil.copy(source, target, autoOverwrite);
        } catch (Exception e) {
            log.error("移动文件失败：moveFileObject：", e);
            return false;
        }
        File result = new File(target, source.getName());
        if (!result.exists()) {
            return false;
        }
        //移动成功在删除掉源文件
        FileUtil.del(source);

        list.stream().filter(v -> StrUtil.isNotBlank(v.getRealPath()))
                .forEach(fileObjectMapper::updateById);
        return true;
    }


    /**
     * 复制文件对象
     *
     * @param source       来源
     * @param targetFolder 目标文件夹
     * @param isOverwrite  是否覆盖
     * @param logicCopy    是否逻辑复制
     * @return {@link FileObject}
     */
    public boolean copyFileObject(FileObject source, FileObject targetFolder, boolean isOverwrite, boolean logicCopy) {
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
        List<FileObject> fileList = fileObjectMapper.selectAllByStartPath(source.getFilePath());
        for (FileObject object : fileList) {
            object.setBucketsId(targetFolder.getBucketsId());
            object.setCreateTime(LocalDateTime.now());
            object.setId(null);
            object.setUpdateTime(LocalDateTime.now());
            object.setCreateUser(SessionInfo.getAndNull().getUid());
            String filePath = object.getFilePath();
            filePath = targetFolder.getFilePath() + filePath.substring(source.getFilePath().length());
            object.setFilePath(filePath);
        }
        if (!logicCopy) {
            //物理复制
            Set<String> delRulePaths = new HashSet<>();
            for (FileObject object : fileList) {
                String realPath = object.getRealPath();
                realPath = targetFolder.getRealPath() + realPath.substring(source.getRealPath().length());
                if (FileUtil.exist(realPath)) {
                    if (isOverwrite) {
                        //如果覆盖
                        delRulePaths.add(realPath);
                    } else {
                        //不覆盖
                        realPath = null;
                    }
                }
                object.setRealPath(realPath);
            }
            if (!delRulePaths.isEmpty()) {
                fileObjectMapper.deleteByRealPathAndBucketsId(targetFolder.getBucketsId(), delRulePaths);
            }
            fileList = fileList.stream().filter(v -> StrUtil.isNotBlank(v.getRealPath())).toList();
            FileUtil.copy(src, target, isOverwrite);
        }
        fileList.forEach(fileObjectMapper::insert);
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
        File target = FileUtil.rename(file, newName, true);

        String filePath = fileObject.getFilePath();
        filePath = filePath.substring(0, filePath.lastIndexOf(DELIMITER) + 1) + file.getName();
        String realPath = fileObject.getRealPath();

        if (fileObject.getIsDir()) {
            // 是目录
            // 查询子文件列表
            List<FileObject> list = fileObjectMapper.selectAllByStartPath(fileObject.getFilePath());
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
                fileObjectMapper.updateById(object);
            }
        }

        fileObject.setFileName(file.getName());
        fileObject.setFilePath(filePath);
        fileObject.setRealPath(target.getAbsolutePath());
        fileObject.setUpdateUser(SessionInfo.getAndNull().getUid());
        fileObject.setUpdateTime(LocalDateTime.now());
        fileObjectMapper.updateById(fileObject);
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
        if (dirList == null) {
            log.error("fileBlockMargin list为空");
            throw new BizException(RespCode.FILE_ERROR);
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
            resultFileObject.setFileContentType(FileUtil.extName(result));
            resultFileObject.setFilePath(getPath(parentFileObject.getFilePath(), result.getName()));
            resultFileObject.setRealPath(result.getAbsolutePath());
            resultFileObject.setFileSize(FileUtil.size(result));
            resultFileObject.setFileMd5(records.getFileMd5());
            resultFileObject.setIsDir(false);
            resultFileObject.setParentId(parentFileObject.getId());
            resultFileObject.setCreateTime(LocalDateTime.now());
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
        return parentPath.concat(DELIMITER).concat(targetName);
    }

}
