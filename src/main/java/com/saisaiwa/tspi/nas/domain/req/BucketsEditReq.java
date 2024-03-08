package com.saisaiwa.tspi.nas.domain.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 10:16
 * @Version：1.0
 */
@Data
public class BucketsEditReq {

    private Long id;

    /**
     * 桶名称
     */
    @NotBlank
    private String bucketsName;

    /**
     * 挂载点路径
     */
    @NotBlank
    private String mountPoint;

    /**
     * 资源ID
     */
    @NotNull
    private Long resId;

    /**
     * 权限:0私有,1公读公写,2公读私写
     */
    @NotNull
    private Integer permissions;

    /**
     * 权限范围:0私有,1资源内公开,2全公开,
     */
    @NotNull
    private Integer permissionsScope;

    /**
     * 是否是静态页面
     */
    @NotNull
    private Boolean staticPage;

}
