package com.saisaiwa.tspi.nas.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.URLUtil;
import com.saisaiwa.tspi.nas.common.anno.CheckBucketsAcl;
import com.saisaiwa.tspi.nas.common.bean.BaseResponse;
import com.saisaiwa.tspi.nas.common.bean.IdReq;
import com.saisaiwa.tspi.nas.common.bean.PageBodyResponse;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.domain.entity.FileBlockRecords;
import com.saisaiwa.tspi.nas.domain.enums.BucketsACLEnum;
import com.saisaiwa.tspi.nas.domain.file.*;
import com.saisaiwa.tspi.nas.domain.req.BucketsQueryReq;
import com.saisaiwa.tspi.nas.domain.req.FileObjectSignReq;
import com.saisaiwa.tspi.nas.domain.req.FileShareListQueryReq;
import com.saisaiwa.tspi.nas.domain.vo.*;
import com.saisaiwa.tspi.nas.service.BucketsService;
import com.saisaiwa.tspi.nas.service.FileObjectService;
import com.saisaiwa.tspi.nas.service.FileShareService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Resource
    private FileShareService fileShareService;

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
     * 获取签名-下载
     *
     * @param req
     * @return
     */
    @PostMapping("/sign/download")
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ})
    public BaseResponse<FileObjectSignVo> signFileObject(@Validated @RequestBody FileObjectSignReq req, HttpServletResponse response) {
        setBucketId(req);
        Assert.notNull(req.getObjectId());
        Assert.notBlank(req.getUuid());
        req.setType(0);
        return toSignResponse(req, response);
    }


    /**
     * 获取签名-上传
     *
     * @param req
     * @return
     */
    @PostMapping("/sign/upload")
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ, BucketsACLEnum.PUT_OBJ})
    public BaseResponse<FileObjectSignVo> signFileObjectUpload(@Validated @RequestBody FileObjectSignReq req, HttpServletResponse response) {
        setBucketId(req);
        req.setType(1);
        Assert.notNull(req.getObjectId());
        Assert.notBlank(req.getUuid());
        return toSignResponse(req, response);
    }

    private BaseResponse<FileObjectSignVo> toSignResponse(FileObjectSignReq req, HttpServletResponse response) {
        FileObjectSignVo signVo = fileObjectService.signFileObject(req);
        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(signVo);
        stringObjectMap.put("fileName", URLUtil.encodeAll(signVo.getFileName()));
        stringObjectMap.forEach((k, v) -> {
            Cookie cookie = new Cookie(k, Optional.ofNullable(v.toString()).orElse(""));
            cookie.setMaxAge(24 * 60 * 60); // 设置Cookie的最大存活时间为1天
            cookie.setPath("/"); // 设置Cookie的路径
            response.addCookie(cookie);
        });
        return BaseResponse.ok(signVo);
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
     * 支持预览
     *
     * @return {@link ResponseEntity}<{@link InputStreamResource}>
     */
    @GetMapping("/preview")
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ})
    @ResponseBody
    public ResponseEntity<InputStreamResource> preview(FObjectGet dat) {
        setBucketId(dat);
        Assert.notNull(dat.getObjectId());
        dat.setDownload(false);
        return fileObjectService.getFileObjectStream(dat, null);
    }

    /**
     * 创建文件外链分享
     *
     * @param dat
     */
    @PostMapping("/share")
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ, BucketsACLEnum.SHARE_OBJ})
    public BaseResponse<FileShareInfoVo> createObjectShare(@RequestBody @Validated FObjectShare dat) {
        setBucketId(dat);
        return BaseResponse.ok(fileShareService.createObjectShare(dat));
    }


    /**
     * 获取用户的创建分享数据
     *
     * @param req
     * @return
     */
    @GetMapping("/share")
    @CheckBucketsAcl({BucketsACLEnum.GET_OBJ, BucketsACLEnum.SHARE_OBJ})
    public BaseResponse<PageBodyResponse<FileShareInfoVo>> getMyShareAll(FileShareListQueryReq req) {
        req.setBucketsId(SessionInfo.get().getBucketPermission().getBucketsId());
        req.setUid(SessionInfo.get().getUid());
        return BaseResponse.ok(fileShareService.getMyShareAll(req));
    }
}
