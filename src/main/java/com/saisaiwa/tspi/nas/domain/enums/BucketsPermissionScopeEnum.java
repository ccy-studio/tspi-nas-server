package com.saisaiwa.tspi.nas.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 权限范围:0私有,1资源内公开,2全公开
 *
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 11:12
 * @Version：1.0
 */
@AllArgsConstructor
@Getter
@SuppressWarnings("all")
public enum BucketsPermissionScopeEnum {

    /**
     * 0私有
     */
    PS_PRIVATE(0),

    /**
     * 1资源内公开
     */
    PS_GROUP(1),

    /**
     * 2全公开
     */
    PS_ALL(2);


    private final int premiss;

}
