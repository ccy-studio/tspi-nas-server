package com.saisaiwa.tspi.nas.domain.req;

import lombok.Data;

/**
 * @description:
 * @date: 2024/03/18 10:46
 * @author: saisiawa
 **/
@Data
public class FileObjectShareGetReq {

    /**
     * 访问key
     */
    private String key;

    /**
     * 密码
     */
    private String pwd;

    /**
     * 是否强制转直链
     */
    private boolean forceSymlink;

    /**
     * Head内的分段下载参数
     */
    private String range;

    /**
     * 是否下载还是预览
     */
    private boolean download;

    /**
     * 转发的Url
     */
    private String forwardUri;
}
