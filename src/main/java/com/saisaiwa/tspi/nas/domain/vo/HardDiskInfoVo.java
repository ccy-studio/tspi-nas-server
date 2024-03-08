package com.saisaiwa.tspi.nas.domain.vo;

import lombok.Data;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 9:46
 * @Version：1.0
 */
@Data
public class HardDiskInfoVo {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 硬盘挂载点路径
     */
    private String mountPath;

    /**
     * 设备名称/sdax
     */
    private String device;

    /**
     * 硬盘唯一ID标识
     */
    private String diskId;

    /**
     * 创建时间
     */
    private String createTime;
}
