package com.saisaiwa.tspi.nas.domain.vo;

import lombok.Data;

/**
 * @description:
 * @date: 2024/03/18 14:01
 * @author: saisiawa
 **/
@Data
public class FileShareInfoVo {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 分享签名KEY
     */
    private String signKey;

    /**
     * 过期时间,为空永久
     */
    private String expirationTime;

    /**
     * 访问密码
     */
    private String accessPassword;

    /**
     * 访问次数
     */
    private Long clickCount;

    /**
     * 是否是直链
     */
    private Boolean isSymlink;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 桶名
     */
    private String bucketsName;

    /**
     * 文件大小
     */
    private long fileSize;

}
