package com.saisaiwa.tspi.nas.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 权限:0私有,1公读公写,2公读私写
 *
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 11:12
 * @Version：1.0
 */
@AllArgsConstructor
@Getter
@SuppressWarnings("all")
public enum BucketsPermissionEnum {

    /**
     * 0私有
     */
    PR_PRIVATE(0),

    /**
     * 1公读公写
     */
    PR_RW(1),

    /**
     * 2公读私写
     */
    PR_R(2);

    private final int premiss;

}
