package com.saisaiwa.tspi.nas.domain.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 9:43
 * @Version：1.0
 */
@Data
public class HardDiskEditReq {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 硬盘挂载点路径
     */
    @NotBlank
    private String mountPath;

    /**
     * 设备名称/sdax
     */
    @NotBlank
    private String device;

    /**
     * 硬盘唯一ID标识
     */
    @NotBlank
    private String diskId;

}
