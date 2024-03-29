package com.saisaiwa.tspi.nas.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.saisaiwa.tspi.nas.common.bean.BaseResponse;
import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.exception.BizException;
import com.saisaiwa.tspi.nas.common.util.JSONUtil;
import com.saisaiwa.tspi.nas.domain.entity.FileObjectShare;
import com.saisaiwa.tspi.nas.domain.req.FileObjectShareGetReq;
import com.saisaiwa.tspi.nas.domain.req.FileSharePasswordReq;
import com.saisaiwa.tspi.nas.domain.req.FileSignReq;
import com.saisaiwa.tspi.nas.service.FileObjectService;
import com.saisaiwa.tspi.nas.service.FileShareService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @description:
 * @date: 2024/03/18 11:19
 * @author: saisiawa
 **/
@Controller
@RequestMapping("/share")
@Validated
public class FileShareController {

    @Resource
    private FileObjectService fileObjectService;

    @Resource
    private FileShareService fileShareService;


    /**
     * 获取分享文件
     *
     * @param key
     * @return
     */
    @GetMapping("/object/{key}")
    public ResponseEntity<?> getShareFileObject(@NotBlank @PathVariable String key,
                                                @RequestParam(required = false, value = "d", defaultValue = "false") boolean download,
                                                @RequestHeader(required = false, value = "range") String range,
                                                @RequestParam(required = false, value = "pwd") String password) {
        FileObjectShareGetReq req = new FileObjectShareGetReq();
        req.setKey(key);
        req.setDownload(download);
        req.setPwd(password);
        req.setRange(range);
        return fileObjectService.getShareFileObject(req);
    }

    /**
     * Web获取下载流
     *
     * @param signStr 签名
     * @param range
     * @return
     */
    @GetMapping("/symlink/{signStr}")
    public ResponseEntity<?> getShareFileStream(@NotBlank @PathVariable String signStr, @RequestHeader(required = false, value = "range") String range) {
        String str = Base64.decodeStr(signStr);
        FileSignReq signReq = JSONUtil.parseObject(str, FileSignReq.class);
        if (signReq == null) {
            throw new BizException(RespCode.INVALID_PARAMS);
        }
        if (signReq.getExpireTime() < System.currentTimeMillis()) {
            throw new BizException("链接已过期");
        }
        if (StrUtil.isBlank(signReq.getSignString())) {
            throw new BizException(RespCode.INVALID_PARAMS);
        }
        FileObjectShare share = fileShareService.getByKey(signReq.getObjectKey());
        if (share == null) {
            throw new BizException("分享不存在");
        }
        String sha256 = SecureUtil.sha256(signReq.getObjectKey() + Optional.ofNullable(share.getAccessPassword()).orElse("") + signReq.getExpireTime());
        if (!sha256.equals(signReq.getSignString())) {
            throw new BizException("非法请求");
        }
        FileObjectShareGetReq req = new FileObjectShareGetReq();
        req.setKey(signReq.getObjectKey());
        req.setDownload(true);
        req.setPwd(share.getAccessPassword());
        req.setRange(range);
        req.setForceSymlink(true);
        return fileObjectService.getShareFileObject(req);
    }

    /**
     * 校验密码
     *
     * @param req
     * @return
     */
    @ResponseBody
    @PostMapping("/validate")
    public BaseResponse<Boolean> checkPassword(@RequestBody @Validated FileSharePasswordReq req) {
        FileObjectShare share = fileShareService.getByKey(req.getShareKey());
        if (share == null) {
            return BaseResponse.fail(RespCode.INVALID_PARAMS);
        }
        return BaseResponse.ok(SecureUtil.md5(req.getPassword()).equals(SecureUtil.md5(share.getAccessPassword())));
    }
}
