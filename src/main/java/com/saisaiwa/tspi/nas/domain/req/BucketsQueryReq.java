package com.saisaiwa.tspi.nas.domain.req;

import lombok.Getter;
import lombok.Setter;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 10:32
 * @Version：1.0
 */
@Setter
@Getter
public class BucketsQueryReq{

    private String keyword;

    /**
     * 是否返回用户ACL
     */
    private Boolean displayPermission;

    private Long id;
}
