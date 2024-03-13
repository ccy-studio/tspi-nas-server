package com.saisaiwa.tspi.nas.domain.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @description:
 * @date: 2024/03/13 16:09
 * @author: saisiawa
 **/
@Data
public class FileObjectUploadAuthReq {

    /**
     * 描述本次签名的有效时间ISO 8601 UTC
     * 格式： yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     */
    @NotBlank
    private String expiration;

    /**
     * 桶名
     */
    @NotBlank
    private String bucketName;

    /**
     * 用户的AK
     */
    @NotBlank
    private String accessKey;


    /**
     * 父文件夹ID
     */
    @NotNull
    private Long folderId;


    /**
     * 临时签名Key
     */
    private String securityToken;
}
