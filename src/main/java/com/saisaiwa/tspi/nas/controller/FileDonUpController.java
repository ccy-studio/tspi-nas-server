package com.saisaiwa.tspi.nas.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.saisaiwa.tspi.nas.common.bean.BaseResponse;
import com.saisaiwa.tspi.nas.common.bean.SessionInfo;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.common.util.SpringUtils;
import com.saisaiwa.tspi.nas.domain.entity.User;
import com.saisaiwa.tspi.nas.domain.file.FObjectGet;
import com.saisaiwa.tspi.nas.domain.file.FObjectUploadBlock;
import com.saisaiwa.tspi.nas.domain.vo.FileBlockInfoVo;
import com.saisaiwa.tspi.nas.service.FileObjectService;
import com.saisaiwa.tspi.nas.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * 文件上传下载
 *
 * @description:
 * @date: 2024/03/26 10:45
 * @author: saisiawa
 **/
@Controller
@RequestMapping("/file/s")
@Slf4j
public class FileDonUpController {

    @Resource
    private FileObjectService fileObjectService;

    @Resource
    private UserService userService;

    /**
     * 上传块文件
     *
     * @param file
     * @param dat
     * @return
     */
    @PostMapping("/object/block")
    @ResponseBody
    public BaseResponse<FileBlockInfoVo> uploadFileBlock(MultipartFile file, FObjectUploadBlock dat) {
        FileParams params = checkSign(1);
        dat.setBucketId(params.bucketsId);
        if (dat.getBlockId() == null || StrUtil.isBlank(dat.getFileName())) {
            return BaseResponse.fail(RespCode.INVALID_PARAMS);
        }
        return BaseResponse.ok(fileObjectService.uploadFileBlock(file, dat));
    }

    /**
     * 获取文件对象流
     * 支持预览，支持分段下载
     *
     * @return {@link ResponseEntity}<{@link InputStreamResource}>
     */
    @GetMapping("/download/{fileName}")
    @ResponseBody
    public ResponseEntity<InputStreamResource> getFileObjectStream(@PathVariable String fileName, @RequestHeader(value = "Range", required = false) String range,
                                                                   @RequestHeader(value = "X-DESC", required = false, defaultValue = "false") boolean download) {
        log.info("文件下载 ：filename:{} Header：range：{}, X-DESC：{}", fileName, range, download);
        Iterator<String> iterator = SpringUtils.getRequest().getHeaderNames().asIterator();
        while (iterator.hasNext()) {
            String name = iterator.next();
            log.info("Header--->>> Name:{},Value:{}", name, SpringUtils.getRequest().getHeader(name));
        }
        FileParams params = checkSign(0);
        FObjectGet get = new FObjectGet();
        get.setObjectId(params.objectId);
        get.setBucketId(params.bucketsId);
        get.setDownload(download);
        return fileObjectService.getFileObjectStream(get, range);
    }

    /**
     * 校验签名
     * 加密方法请看：com.saisaiwa.tspi.nas.service.impl.FileObjectServiceImpl#signFileObject
     *
     * @param type 类型上传为1，下载为0
     */
    private FileParams checkSign(int type) {
        String sign = getRequestValue("X-SIGN");
        if (StrUtil.isBlank(sign)) {
            throw new BizException(RespCode.INVALID_PARAMS);
        }
        String account = getRequestValue("account");
        String bucketId = getRequestValue("bucketId");
        String objectId = getRequestValue("objectId");
        String uuid = getRequestValue("uuid");
        String expire = getRequestValue("expire");
        if (StrUtil.isBlank(account) || StrUtil.isBlank(bucketId) || StrUtil.isBlank(objectId) || StrUtil.isBlank(uuid) || StrUtil.isBlank(expire)) {
            throw new BizException(RespCode.INVALID_PARAMS);
        }
        //获取用户
        User user = userService.getUserByAccount(account);
        Assert.notNull(user);
        //签名
        StringJoiner stringJoiner = new StringJoiner(";");
        stringJoiner.add(uuid)
                .add(objectId)
                .add(bucketId)
                .add(expire)
                .add(user.getSecretKey() + account)
                .add(type + "");
        String sha256 = SecureUtil.sha256(stringJoiner.toString());
        if (!sha256.equals(sign)) {
            throw new BizException("签名校验失败");
        }
        long time = NumberUtil.parseLong(expire);
        //如果是下载才去校验是否到期
        if (System.currentTimeMillis() > time && type == 0) {
            throw new BizException("签名已过期");
        }

        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setUser(user);
        sessionInfo.setUid(user.getId());
        sessionInfo.setAk(user.getAccessKey());
        sessionInfo.setSk(user.getSecretKey());
        SessionInfo.set(sessionInfo);

        return new FileParams(Long.parseLong(objectId), Long.parseLong(bucketId));
    }

    private String getRequestValue(String key) {
        HttpServletRequest request = SpringUtils.getRequest();
        String val = request.getHeader(key);
        if (StrUtil.isBlank(val)) {
            val = request.getParameter(key);
        }
        if (StrUtil.isBlank(val) && request.getCookies() != null) {
            val = Arrays.stream(request.getCookies()).filter(f -> Objects.equals(f.getName(), key))
                    .map(Cookie::getValue).findAny().orElse(null);
        }
        return val;
    }

    private record FileParams(Long objectId, Long bucketsId) {
    }

}
