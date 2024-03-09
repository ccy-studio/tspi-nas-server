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
