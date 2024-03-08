package com.saisaiwa.tspi.nas.domain.vo;

import lombok.Data;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 10:36
 * @Version：1.0
 */
@Data
public class BucketsInfoVo {

    /**
     * 桶名称
     */
    private String bucketsName;

    /**
     * 挂载点路径
     */
    private String mountPoint;

    /**
     * 资源ID
     */
    private Long resId;

    /**
     * 资源名称
     */
    private String resName;
}
