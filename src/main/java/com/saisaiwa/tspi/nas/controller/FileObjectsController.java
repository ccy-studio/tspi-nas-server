package com.saisaiwa.tspi.nas.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.saisaiwa.tspi.nas.common.anno.CheckBucketsAcl;
import com.saisaiwa.tspi.nas.common.bean.BaseResponse;
import com.saisaiwa.tspi.nas.common.bean.IdReq;
import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.file.FileRangeInputStream;
import com.saisaiwa.tspi.nas.domain.entity.FileBlockRecords;
import com.saisaiwa.tspi.nas.domain.enums.BucketsACLEnum;
import com.saisaiwa.tspi.nas.domain.file.*;
import com.saisaiwa.tspi.nas.domain.req.BucketsQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.BucketsInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.FileBlockInfoVo;
import com.saisaiwa.tspi.nas.domain.vo.FileObjectInfoVo;
import com.saisaiwa.tspi.nas.service.BucketsService;
import com.saisaiwa.tspi.nas.service.FileObjectService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件对象
 *
 * @description:
 * @date: 2024/03/15 16:24
 * @author: saisiawa
 **/
@RestController
@RequestMapping("/fs")
@Validated
public class FileObjectsController {

    @Resource
    private FileObjectService fileObjectService;

    @Resource
    private BucketsService bucketsService;

    private void setBucketId(FBaseEntity entity) {
        entity.setBucketId(SessionInfo.get().getBucketPermission().getBucketsId());
    }


    /**
     * 查询此用户可见的存储桶
     *
     * @param req
     * @return
     */
    @GetMapping("/buckets")
    public BaseResponse<List<BucketsInfoVo>> getBucketAll(BucketsQueryReq req) {
        return BaseResponse.ok(bucketsService.getBucketAll(req));
    }


    /**
     * 查询获取对象列表
     *
     * @param search
     * @return
     */
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ})
    @GetMapping("/object")
    public BaseResponse<PageBodyResponse<FileObjectInfoVo>> getObjectFileList(FObjectSearch search) {
        search.setBucketId(SessionInfo.get().getBucketPermission().getBucketsId());
        return BaseResponse.ok(fileObjectService.selectObjectAll(search));
    }


    /**
     * 删除对象
     *
     * @param dat dat
     */
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ, BucketsACLEnum.DEL_OBJ})
    @DeleteMapping("/object")
    public BaseResponse<Void> deleteObject(@Validated @RequestBody FObjectDelete dat) {
        setBucketId(dat);
        Assert.notNull(dat.getObjectId());
        fileObjectService.deleteObject(dat);
        return BaseResponse.ok();
    }

    /**
     * 文件复制
     *
     * @param dat
     */
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ, BucketsACLEnum.PUT_OBJ})
    @PutMapping("/object/copy")
    public BaseResponse<Void> copyFile(@Validated @RequestBody FObjectCopy dat) {
        setBucketId(dat);
        Assert.notNull(dat.getObjectId());
        fileObjectService.copyFile(dat);
        return BaseResponse.ok();
    }

    /**
     * 移动文件
     *
     * @param dat
     */
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ, BucketsACLEnum.PUT_OBJ})
    @PutMapping("/object/move")
    public BaseResponse<Void> moveFile(@Validated @RequestBody FObjectCopy dat) {
        setBucketId(dat);
        Assert.notNull(dat.getObjectId());
        fileObjectService.moveFile(dat);
        return BaseResponse.ok();
    }


    /**
     * 文件对象重命名
     *
     * @param dat
     */
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ, BucketsACLEnum.PUT_OBJ})
    @PutMapping("/object/rename")
    public BaseResponse<Void> rename(@Validated @RequestBody FObjectRename dat) {
        setBucketId(dat);
        Assert.notNull(dat.getObjectId());
        fileObjectService.rename(dat);
        return BaseResponse.ok();
    }

    /**
     * 创建文件夹
     *
     * @param dat
     */
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ, BucketsACLEnum.PUT_OBJ})
    @PutMapping("/object/folder")
    public BaseResponse<Void> createFolder(@Validated @RequestBody FObjectUpload dat) {
        setBucketId(dat);
        fileObjectService.createFolder(dat);
        return BaseResponse.ok();
    }

    /**
     * 检查文件是否存在
     *
     * @param has
     * @return
     */
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ, BucketsACLEnum.PUT_OBJ})
    @GetMapping("/object/has")
    public BaseResponse<Boolean> hasFile(@Validated FObjectHas has) {
        setBucketId(has);
        return BaseResponse.ok(fileObjectService.hasFile(has));
    }

    /**
     * 上传单体文件
     *
     * @param file
     * @param dat
     */
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ, BucketsACLEnum.PUT_OBJ})
    @PostMapping("/object")
    public BaseResponse<Void> uploadFileSign(MultipartFile file, @Validated FObjectUpload dat) {
        setBucketId(dat);
        if (file == null || file.isEmpty()) {
            return BaseResponse.fail(RespCode.INVALID_PARAMS);
        }
        fileObjectService.uploadFileSign(file, dat);
        return BaseResponse.ok();
    }


    /**
     * 初始化分块上传
     *
     * @param dat
     * @return
     */
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ, BucketsACLEnum.PUT_OBJ})
    @PostMapping("/object/block/init")
    public BaseResponse<FileBlockRecords> initUploadBlock(@Validated @RequestBody FObjectUploadBlock dat) {
        setBucketId(dat);
        return BaseResponse.ok(fileObjectService.initUploadBlock(dat));
    }

    /**
     * 获取当前Block的信息
     *
     * @param blockId
     * @return
     */
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ, BucketsACLEnum.PUT_OBJ})
    @GetMapping("/object/block")
    public BaseResponse<FileBlockInfoVo> getBlockInfo(@NotNull Long blockId) {
        FileBlockInfoVo infoVo = fileObjectService.getBlockInfo(blockId);
        return infoVo == null ? BaseResponse.fail() : BaseResponse.ok(infoVo);
    }


    /**
     * 上传块文件
     *
     * @param file
     * @param dat
     * @return
     */
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ, BucketsACLEnum.PUT_OBJ})
    @PostMapping("/object/block")
    public BaseResponse<FileBlockInfoVo> uploadFileBlock(MultipartFile file, FObjectUploadBlock dat) {
        setBucketId(dat);
        if (dat.getBlockId() == null || StrUtil.isBlank(dat.getFileName()) || dat.getBucketId() == null) {
            return BaseResponse.fail(RespCode.INVALID_PARAMS);
        }
        return BaseResponse.ok(fileObjectService.uploadFileBlock(file, dat));
    }

    /**
     * 分块上传的文件进行合并
     *
     * @param idReq
     * @return
     */
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ, BucketsACLEnum.PUT_OBJ})
    @PutMapping("/object/block")
    public BaseResponse<FileObjectInfoVo> fileBlockMerge(@RequestBody @Validated IdReq<Long> idReq) {
        FileObjectInfoVo infoVo = fileObjectService.fileBlockMerge(idReq.getId());
        return infoVo == null ? BaseResponse.fail() : BaseResponse.ok(infoVo);
    }


    /**
     * 获取文件对象流
     * 支持预览，支持分段下载
     *
     * @param dat dat
     * @return {@link ResponseEntity}<{@link FileRangeInputStream}>
     */
    @GetMapping("/preview")
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ})
    @ResponseBody
    public ResponseEntity<InputStreamResource> getFileObjectStream(FObjectGet dat, @RequestHeader(value = "range", required = false) String range) {
        setBucketId(dat);
        Assert.notNull(dat.getObjectId());
        return fileObjectService.getFileObjectStream(dat, range);
    }

}
