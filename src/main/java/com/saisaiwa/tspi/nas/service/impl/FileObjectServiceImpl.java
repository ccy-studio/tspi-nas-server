package com.saisaiwa.tspi.nas.service.impl;

import cn.hutool.core.lang.Assert;
import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.common.file.FileNativeService;
import com.saisaiwa.tspi.nas.config.SystemConfiguration;
import com.saisaiwa.tspi.nas.domain.convert.FileObjectConvert;
import com.saisaiwa.tspi.nas.domain.entity.FileObject;
import com.saisaiwa.tspi.nas.domain.file.FObjectCopy;
import com.saisaiwa.tspi.nas.domain.file.FObjectDelete;
import com.saisaiwa.tspi.nas.domain.file.FObjectRename;
import com.saisaiwa.tspi.nas.domain.file.FObjectSearch;
import com.saisaiwa.tspi.nas.domain.vo.FileObjectInfoVo;
import com.saisaiwa.tspi.nas.mapper.FileBlockRecordsMapper;
import com.saisaiwa.tspi.nas.mapper.FileObjectMapper;
import com.saisaiwa.tspi.nas.mapper.FileObjectShareMapper;
import com.saisaiwa.tspi.nas.service.FileObjectService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @date: 2024/03/13 15:55
 * @author: saisiawa
 **/
@Service
@Transactional(rollbackFor = Exception.class)
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
            throw new BizException(RespCode.INVALID_PARAMS);
        }
        if (!fileNativeService.deleteFileObject(fileObject)) {
            throw new BizException("删除失败");
        }
        fileObjectMapper.deleteAllByPath(fileObject.getFilePath());
    }


    public void copyFile(FObjectCopy dat) {
        FileObject fileObject = fileObjectMapper.getByIdNoDel(dat.getObjectId());
        Assert.notNull(fileObject);
        FileObject targetObject = fileObjectMapper.getByIdNoDel(dat.getTargetObject());
        Assert.notNull(targetObject);
        if (!dat.getBucketId().equals(fileObject.getBucketsId())) {
            throw new BizException(RespCode.INVALID_PARAMS);
        }
        if (!fileObject.getBucketsId().equals(targetObject.getBucketsId())) {
            throw new BizException(RespCode.INVALID_PARAMS);
        }
        if (fileObject.getParentId().equals(targetObject.getParentId())) {
            throw new BizException(RespCode.INVALID_PARAMS);
        }
        if (!targetObject.getIsDir()) {
            throw new BizException(RespCode.INVALID_PARAMS);
        }
        if (!fileNativeService.copyFileObject(fileObject, targetObject, dat.getIsOverwrite(), systemConfiguration.isLogicCopy())) {
            throw new BizException("复制失败");
        }
    }


    public void moveFile(FObjectCopy dat) {
        FileObject fileObject = fileObjectMapper.getByIdNoDel(dat.getObjectId());
        Assert.notNull(fileObject);
        FileObject targetObject = fileObjectMapper.getByIdNoDel(dat.getTargetObject());
        Assert.notNull(targetObject);
        if (!dat.getBucketId().equals(fileObject.getBucketsId())) {
            throw new BizException(RespCode.INVALID_PARAMS);
        }
        if (!fileObject.getBucketsId().equals(targetObject.getBucketsId())) {
            throw new BizException(RespCode.INVALID_PARAMS);
        }
        if (fileObject.getParentId().equals(targetObject.getParentId())) {
            throw new BizException(RespCode.INVALID_PARAMS);
        }
        if (!targetObject.getIsDir()) {
            throw new BizException(RespCode.INVALID_PARAMS);
        }
        if (!fileNativeService.moveFileObject(fileObject, targetObject, dat.getIsOverwrite())) {
            throw new BizException("复制失败");
        }
    }


    /**
     * 文件对象重命名
     *
     * @param dat
     */
    public void rename(FObjectRename dat) {
        FileObject fileObject = fileObjectMapper.getByIdNoDel(dat.getObjectId());
        Assert.notNull(fileObject);
        if (!dat.getBucketId().equals(fileObject.getBucketsId())) {
            throw new BizException(RespCode.INVALID_PARAMS);
        }
        if (!fileNativeService.renameFile(fileObject, dat.getNewName())) {
            throw new BizException("重命名失败");
        }
    }

}
