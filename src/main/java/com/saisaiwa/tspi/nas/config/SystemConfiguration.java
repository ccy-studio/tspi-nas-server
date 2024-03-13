package com.saisaiwa.tspi.nas.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/2 16:33
 * @Version：1.0
 */
@Component
@ConfigurationProperties(prefix = "sys")
@Data
public class SystemConfiguration {

    /**
     * 是否逻辑拷贝-可优化空间占用
     */
    private boolean isLogicCopy = false;

    /**
     * 分块上传临时文件的失效时间
     */
    private long blockExpirationTime = 3600;

    /**
     * 默认的重置密码
     */
    private String defaultPassword;

    /**
     * Token加密KEY
     */
    private String accessTokenSecurityKey;

    /**
     * Token有效期
     */
    private long accessTokenTtl;
}
