package com.saisaiwa.tspi.nas.service.impl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.IdUtil;
import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
import com.saisaiwa.tspi.nas.common.exception.FileObjectNotFound;
import com.saisaiwa.tspi.nas.domain.convert.FileObjectConvert;
import com.saisaiwa.tspi.nas.domain.dto.FileObjectShareExtDto;
import com.saisaiwa.tspi.nas.domain.entity.FileObject;
import com.saisaiwa.tspi.nas.domain.entity.FileObjectShare;
import com.saisaiwa.tspi.nas.domain.file.FObjectShare;
import com.saisaiwa.tspi.nas.domain.req.FileShareListQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.FileShareInfoVo;
import com.saisaiwa.tspi.nas.mapper.FileObjectMapper;
import com.saisaiwa.tspi.nas.mapper.FileObjectShareMapper;
import com.saisaiwa.tspi.nas.service.FileShareService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description:
 * @date: 2024/03/18 11:35
 * @author: saisiawa
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class FileShareServiceImpl implements FileShareService {

    @Resource
    private FileObjectShareMapper fileObjectShareMapper;

    @Resource
    private FileObjectMapper fileObjectMapper;

    @Override
    public FileObjectShare getByKey(String key) {
        return fileObjectShareMapper.getBySignKey(key);
    }

    /**
     * 创建文件外链分享
     *
     * @param dat
     */
    @Override
    public FileShareInfoVo createObjectShare(FObjectShare dat) {
        FileObject fileObject = fileObjectMapper.getByIdNoDel(dat.getObjectId());
        if (fileObject == null || fileObject.getIsDir()) {
            throw new FileObjectNotFound();
        }
        if (!fileObject.getBucketsId().equals(dat.getBucketId())) {
            throw new FileObjectNotFound();
        }
        //查询是否已设置了分享
        FileObjectShare share = fileObjectShareMapper.getUserShareByObjectId(fileObject.getId(), SessionInfo.get().getUid());
        if (share == null) {
            share = new FileObjectShare();
            share.setCreateUser(SessionInfo.get().getUid());
            share.setFileObjectId(fileObject.getId());
            share.setSignKey(IdUtil.objectId());
        }
        share.setExpirationTime(LocalDateTimeUtil.parse(dat.getExpirationTime()));
        share.setAccessPassword(dat.getAccessPassword());
        share.setIsSymlink(dat.isSymlink());
        if (share.getId() == null) {
            fileObjectShareMapper.insert(share);
        } else {
            fileObjectShareMapper.updateById(share);
        }
        return FileObjectConvert.INSTANCE.toFileShareInfoVo(fileObjectShareMapper.getExtBySignKey(share.getSignKey()));
    }

    /**
     * 获取用户的创建分享数据
     *
     * @param req
     * @return
     */
    @Override
    public PageBodyResponse<FileShareInfoVo> getMyShareAll(FileShareListQueryReq req) {
        List<FileObjectShareExtDto> list = fileObjectShareMapper.selectShareFileObjectExt(req);
        return PageBodyResponse.convert(req, FileObjectConvert.INSTANCE.toFileShareInfoVo(list));
    }
}
