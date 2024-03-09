package com.saisaiwa.tspi.nas.domain.vo;

import lombok.Data;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/9 15:44
 * @Version：1.0
 */
@Data
public class BucketsPermissionUserVo {
    /**
     * 存储桶ID
     */
    private Long bucketsId;

    /**
     * 是否可读
     */
    private boolean read;

    /**
     * 是否可写
     */
    private boolean write;

    /**
     * 是否可删
     */
    private boolean delete;

    /**
     * 是否可分享
     */
    private boolean share;

    /**
     * 是否可管理
     */
    private boolean manage;
}
