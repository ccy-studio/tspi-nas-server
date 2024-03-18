package com.saisaiwa.tspi.nas.domain.req;

import lombok.Data;

/**
 * @description:
 * @date: 2024/03/18 11:30
 * @author: saisiawa
 **/
@Data
public class FileSignReq {

    /**
     * 过期时间
     */
    private long expireTime;

    /**
     * 文件分享Key
     */
    private String objectKey;

    /**
     * Sha256签名
     * 签名规则: SHA256(objectKey+password+expireTime)
     */
    private String signString;

}
