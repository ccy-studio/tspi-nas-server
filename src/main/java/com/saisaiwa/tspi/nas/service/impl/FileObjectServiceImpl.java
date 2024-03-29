package com.saisaiwa.tspi.nas.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.net.URLEncodeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.common.exception.FileObjectNotFound;
import com.saisaiwa.tspi.nas.common.exception.MessageBoxException;
import com.saisaiwa.tspi.nas.common.file.FileBlockEntity;
import com.saisaiwa.tspi.nas.common.file.FileGetNativeInfo;
import com.saisaiwa.tspi.nas.common.file.FileNativeService;
import com.saisaiwa.tspi.nas.common.file.FileRangeInputStream;
import com.saisaiwa.tspi.nas.common.util.SpringUtils;
import com.saisaiwa.tspi.nas.config.SystemConfiguration;
import com.saisaiwa.tspi.nas.domain.convert.FileObjectConvert;
import com.saisaiwa.tspi.nas.domain.entity.FileBlockRecords;
import com.saisaiwa.tspi.nas.domain.entity.FileObject;
import com.saisaiwa.tspi.nas.domain.entity.FileObjectShare;
import com.saisaiwa.tspi.nas.domain.file.*;
import com.saisaiwa.tspi.nas.domain.req.FileObjectShareGetReq;
import com.saisaiwa.tspi.nas.domain.req.FileObjectSignReq;
import com.saisaiwa.tspi.nas.domain.vo.FileBlockInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.FileObjectInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.FileObjectSignVo;
import com.saisaiwa.tspi.nas.mapper.FileBlockRecordsMapper;
import com.saisaiwa.tspi.nas.mapper.FileObjectMapper;
import com.saisaiwa.tspi.nas.mapper.FileObjectShareMapper;
import com.saisaiwa.tspi.nas.service.FileObjectService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;

/**
 * @description:
 * @date: 2024/03/13 15:55
 * @author: saisiawa
 **/
@Service
@Slf4j
public class FileObjectServiceImpl implements FileObjectService {

    @Resource
    private FileObjectMapper fileObjectMapper;

    @Resource
    private FileObjectShareMapper objectShareMapper;

    @Resource
    private FileBlockRecordsMapper blockRecordsMapper;

    @Resource
    private FileNativeService fileNativeService;

    @Resource
    private SystemConfiguration systemConfiguration;


    /**
     * 查询1级文件列表
     *
     * @param search 搜索
     * @return {@link PageBodyResponse}<{@link FileObjectInfoVo}>
     */
    @Override
    public PageBodyResponse<FileObjectInfoVo> selectObjectAll(FObjectSearch search) {
        List<FileObject> list = fileObjectMapper.searchFileObject(search);
        List<FileObjectInfoVo> infoVos = FileObjectConvert.INSTANCE.toFileObjectInfoVo(list);
        return PageBodyResponse.convert(search, infoVos);
    }


    /**
     * 删除对象
     *
     * @param dat dat
     */
    @Override
    public void deleteObject(FObjectDelete dat) {
        FileObject fileObject = fileObjectMapper.getByIdNoDel(dat.getObjectId());
        Assert.notNull(fileObject);
        if (!dat.getBucketId().equals(fileObject.getBucketsId())) {
            throw new FileObjectNotFound();
        }
        if (!fileNativeService.deleteFileObject(fileObject)) {
            throw new BizException("删除失败");
        }
        fileNativeService.lockAccept(dat.getBucketId(), v -> {
            fileObjectMapper.deleteAllByFilePathAndStartWith(fileObject.getFilePath());
        });
    }


    /**
     * 文件复制
     *
     * @param dat
     */
    @Override
    public void copyFile(FObjectCopy dat) {
        FileObject fileObject = fileObjectMapper.getByIdNoDel(dat.getObjectId());
        Assert.notNull(fileObject);
        FileObject targetObject = fileObjectMapper.getByIdNoDel(dat.getTargetObject());
        Assert.notNull(targetObject);
        if (!dat.getBucketId().equals(fileObject.getBucketsId())) {
            throw new FileObjectNotFound();
        }
        if (!fileObject.getBucketsId().equals(targetObject.getBucketsId())) {
            throw new FileObjectNotFound();
        }
        if (fileObject.getParentId().equals(targetObject.getId())) {
            //如果要复制的目的地和要复制的问题根目录一样那么就不复制了
            return;
        }
        if (!targetObject.getIsDir()) {
            throw new FileObjectNotFound();
        }
        fileNativeService.lockAccept(dat.getBucketId(), v -> {
            if (!v.copyFileObject(fileObject, targetObject, dat.getIsOverwrite())) {
                throw new BizException("复制失败");
            }
        });
    }


    /**
     * 移动文件
     *
     * @param dat
     */
    @Override
    public void moveFile(FObjectCopy dat) {
        FileObject fileObject = fileObjectMapper.getByIdNoDel(dat.getObjectId());
        Assert.notNull(fileObject);
        FileObject targetObject = fileObjectMapper.getByIdNoDel(dat.getTargetObject());
        Assert.notNull(targetObject);
        if (!dat.getBucketId().equals(fileObject.getBucketsId())) {
            throw new FileObjectNotFound();
        }
        if (!fileObject.getBucketsId().equals(targetObject.getBucketsId())) {
            throw new FileObjectNotFound();
        }
        if (fileObject.getParentId().equals(targetObject.getId())) {
            throw new FileObjectNotFound();
        }
        if (!targetObject.getIsDir()) {
            throw new FileObjectNotFound();
        }
        fileNativeService.lockAccept(dat.getBucketId(), v -> {
            if (!v.moveFileObject(fileObject, targetObject, dat.getIsOverwrite())) {
                throw new BizException("移动失败");
            }
        });
    }


    /**
     * 文件对象重命名
     *
     * @param dat
     */
    @Override
    public void rename(FObjectRename dat) {
        FileObject fileObject = fileObjectMapper.getByIdNoDel(dat.getObjectId());
        Assert.notNull(fileObject);
        if (!dat.getBucketId().equals(fileObject.getBucketsId())) {
            throw new FileObjectNotFound();
        }
        fileNativeService.lockAccept(dat.getBucketId(), v -> {
            if (!fileNativeService.renameFile(fileObject, dat.getNewName())) {
                throw new BizException("重命名失败");
            }
        });
    }

    /**
     * 创建文件夹
     *
     * @param dat
     */
    @Override
    public void createFolder(FObjectUpload dat) {
        FileObject targetObject = fileObjectMapper.getByIdNoDel(dat.getTargetFolder());
        Assert.notNull(targetObject);
        if (!targetObject.getBucketsId().equals(dat.getBucketId())) {
            throw new FileObjectNotFound();
        }
        FileObject fileObject = new FileObject();
        fileObject.setFileName(dat.getFileName());
        fileObject.setBucketsId(targetObject.getBucketsId());
        if (!fileNativeService.createFolderFileObject(fileObject, targetObject, dat.getIsOverwrite())) {
            throw new FileObjectNotFound();
        }
        fileNativeService.lockAccept(dat.getBucketId(), v -> {
            fileObjectMapper.insert(fileObject);
        });
    }

    /**
     * 检查文件是否存在
     *
     * @param has
     * @return
     */
    @Override
    public boolean hasFile(FObjectHas has) {
        FileObject targetObject = fileObjectMapper.getByIdNoDel(has.getTargetObject());
        if (targetObject == null) {
            throw new FileObjectNotFound();
        }
        FileObject object = fileObjectMapper.getByFileNameAndParentId(has.getFileName(), has.getTargetObject());
        if (object != null && !fileNativeService.has(object.getRealPath())) {
            //发现数据不存在
            fileObjectMapper.deleteAllByFilePathAndStartWith(object.getFilePath());
            return false;
        }
        return object != null;
    }


    /**
     * 上传单体文件
     *
     * @param file
     * @param dat
     */
    @Override
    public void uploadFileSign(MultipartFile file, FObjectUpload dat) {
        FileObject targetObject = fileObjectMapper.getByIdNoDel(dat.getTargetFolder());
        if (targetObject == null) {
            throw new FileObjectNotFound();
        }
        if (fileObjectMapper.getByFileNameAndParentId(dat.getFileName(), targetObject.getId()) != null
                && !dat.getIsOverwrite()) {
            // 文件存在且又不允许覆盖那么就自动重命名一下
            dat.setFileName(IdUtil.simpleUUID().concat("_".concat(dat.getFileName())));
        }
        if (!targetObject.getBucketsId().equals(dat.getBucketId())) {
            throw new FileObjectNotFound();
        }
        FileObject fileObject = new FileObject();
        fileObject.setIsDir(false);
        fileObject.setFileName(dat.getFileName());
        fileNativeService.uploadFileObject(file, fileObject, targetObject, dat.getIsOverwrite());
        fileObjectMapper.insert(fileObject);
    }

    /**
     * 初始化分块上传
     *
     * @param dat
     * @return
     */
    @Override
    public FileBlockRecords initUploadBlock(FObjectUploadBlock dat) {
        FileObject targetObject = fileObjectMapper.getByIdNoDel(dat.getTargetFolder());
        Assert.notNull(targetObject);
        FileBlockRecords records = new FileBlockRecords();
        records.setIsOverwrite(dat.getIsOverwrite());
        records.setFileObjectParentId(targetObject.getId());
        records.setFileName(dat.getFileName());
        records.setFileMd5(dat.getMd5());
        records.setFileSize(dat.getFileSize());
        records.setBlockCount(dat.getFileCount());
        records.setCreateTime(LocalDateTime.now());
        records.setUpdateUser(SessionInfo.get().getUid());
        fileNativeService.uploadBlockInit(records);
        blockRecordsMapper.insert(records);
        return records;
    }


    /**
     * 获取当前Block的信息
     *
     * @param blockId
     * @return
     */
    @Override
    public FileBlockInfoVo getBlockInfo(Long blockId) {
        FileBlockRecords records = blockRecordsMapper.selectById(blockId);
        if (records == null) {
            return null;
        }
        FileBlockInfoVo infoVo = FileObjectConvert.INSTANCE.toFileBlockInfoVo(records);
        List<FileBlockEntity> fileBlockFiles = fileNativeService.getFileBlockFiles(records);
        infoVo.setCurrentBlocks(fileBlockFiles);
        return infoVo;
    }


    /**
     * 上传块文件
     *
     * @param file
     * @param dat
     * @return
     */
    @Override
    public FileBlockInfoVo uploadFileBlock(MultipartFile file, FObjectUploadBlock dat) {
        FileBlockRecords records = blockRecordsMapper.selectById(dat.getBlockId());
        Assert.notNull(records);
        fileNativeService.uploadBlock(file, dat.getFileName(), records.getDirPath());
        return getBlockInfo(records.getId());
    }


    /**
     * 分块上传的文件进行合并
     *
     * @param blockId
     * @return
     */
    @Override
    public FileObjectInfoVo fileBlockMerge(Long blockId) {
        FileBlockRecords records = blockRecordsMapper.selectById(blockId);
        if (records == null) {
            return null;
        }
        return FileObjectConvert.INSTANCE.toFileObjectInfoVo(fileNativeService.fileBlockMerge(records));
    }


    /**
     * 获取文件对象流
     * 支持预览，支持分段下载
     *
     * @param dat dat
     * @return {@link ResponseEntity}<{@link FileRangeInputStream}>
     */
    @Override
    public ResponseEntity<InputStreamResource> getFileObjectStream(FObjectGet dat, String range) {
        FileObject targetObject = fileObjectMapper.getByIdNoDel(dat.getObjectId());
        if (targetObject == null || targetObject.getIsDir() || !fileNativeService.has(targetObject.getRealPath())) {
            throw new FileObjectNotFound();
        }
        HttpStatusCode statusCode = HttpStatus.OK;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder(dat.isDownload() ? "attachment" : "inline")
                .filename(URLEncodeUtil.encode(targetObject.getFileName()))
                .build());
        headers.setContentType(MediaType.parseMediaType(targetObject.getFileContentType()));
        if (!dat.isDownload()) {
            range = null;
        }
        FileGetNativeInfo fileGetNativeInfo = fileNativeService.writeOutputFileStream(targetObject, range);
        headers.setContentLength(fileGetNativeInfo.getContentSize());
        if (dat.isDownload()) {
            // 设置Content-Range头部
            headers.set("Accept-Ranges", "bytes");
            headers.set("Content-Range", "bytes " + fileGetNativeInfo.getStart() + "-" + fileGetNativeInfo.getEnd() + "/" + fileGetNativeInfo.getFileSize());
            statusCode = HttpStatus.PARTIAL_CONTENT;
        }
        return ResponseEntity.status(statusCode)
                .headers(headers)
                .body(new InputStreamResource(fileGetNativeInfo.getInputStream()));
    }


    /**
     * 获取分享文件
     *
     * @param req
     * @return
     */
    @Override
    public ResponseEntity<?> getShareFileObject(FileObjectShareGetReq req) {
        FileObjectShare share = objectShareMapper.getBySignKey(req.getKey());
        if (share == null) {
            throw new MessageBoxException("没有找到您访问的资源！");
        }
        if (share.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new MessageBoxException("来晚咯，分享已过期！");
        }
        if (share.getIsSymlink() || req.isForceSymlink()) {
            if (StrUtil.isNotBlank(share.getAccessPassword()) && !share.getAccessPassword().equals(req.getPwd())) {
                throw new BizException("密码不正确！");
            }
            //直链
            FObjectGet get = new FObjectGet();
            get.setDownload(req.isDownload());
            get.setObjectId(share.getFileObjectId());
            return this.getFileObjectStream(get, req.getRange());
        } else {
            //转发
            HttpSession session = SpringUtils.getRequest().getSession();
            session.setAttribute("req", req);
            return ResponseEntity.status(302).location(URI.create(req.getForwardUri())).build();
        }
    }


    /**
     * 获取签名
     *
     * @param req
     * @return
     */
    @Override
    public FileObjectSignVo signFileObject(FileObjectSignReq req) {
        FileObjectSignVo signVo = new FileObjectSignVo();

        String fileName;
        long fileSize;
        //类型上传为1，下载为0
        if (req.getType() == 1) {
            FileBlockRecords records = blockRecordsMapper.selectById(req.getObjectId());
            if (records == null) {
                throw new FileObjectNotFound();
            }
            fileName = records.getFileName();
            fileSize = records.getFileSize();
        } else {
            FileObject fileObject = fileObjectMapper.getByIdNoDel(req.getObjectId());
            if (!fileObject.getBucketsId().equals(req.getBucketId())) {
                throw new FileObjectNotFound();
            }
            if (fileObject.getIsDir()) {
                throw new BizException("文件夹类型无法直接下载");
            }
            fileName = fileObject.getFileName();
            fileSize = fileObject.getFileSize();
        }
        double fileSizeInMb = (double) fileSize / (1024 * 1024);
        double downloadTimeInSeconds = Math.ceil((fileSizeInMb * 8) / 500);
        log.info("========>>>>downloadTimeInSeconds = {}", downloadTimeInSeconds);
        DateTime offset = DateTime.now().offset(DateField.SECOND, (int) Math.max(60, 60 + downloadTimeInSeconds));
        signVo.setBkId(req.getBucketId());
        signVo.setObjectId(req.getObjectId());
        signVo.setFileSize(fileSize);
        signVo.setFileName(fileName);
        signVo.setUuid(req.getUuid());
        signVo.setExpireTime(offset.getTime());

        //签名
        StringJoiner stringJoiner = new StringJoiner(";");
        stringJoiner.add(signVo.getUuid())
                .add(signVo.getObjectId() + "")
                .add(signVo.getBkId() + "")
                .add(signVo.getExpireTime() + "")
                .add(SessionInfo.get().getSk() + SessionInfo.get().getUser().getUserAccount())
                .add(req.getType() + "");
        signVo.setSignString(SecureUtil.sha256(stringJoiner.toString()));
        return signVo;
    }

}
