package com.saisaiwa.tspi.nas.domain.vo;

import lombok.Data;

/**
 * @description:
 * @date: 2024/03/11 15:54
 * @author: saisiawa
 **/
@Data
public class BucketsDetailVo {

    /**
     * 自增主键
     */
    private Long id;

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
     * 权限:0私有,1公读公写,2公读私写
     */
    private Integer permissions;

    /**
     * 权限范围:0私有,1资源内公开,2全公开,
     */
    private Integer permissionsScope;

    /**
     * 是否是静态页面
     */
    private Boolean staticPage;
}
