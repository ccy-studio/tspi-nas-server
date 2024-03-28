package com.saisaiwa.tspi.nas.domain.vo;

import lombok.Data;

/**
 * @description:
 * @date: 2024/03/26 10:07
 * @author: saisiawa
 **/
@Data
public class FileObjectSignVo {

    /**
     * SHA256签名
     */
    private String signString;

    /**
     * 过期时间
     */
    private long expireTime;

    /**
     * 随机字符串
     */
    private String uuid;

    /**
     * 对象ID
     */
    private Long objectId;

    /**
     * 桶ID
     */
    private Long bkId;

    /**
     * 文件大小
     */
    private long fileSize;

    /**
     * 文件名
     */
    private String fileName;

}
