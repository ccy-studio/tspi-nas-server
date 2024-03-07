package com.saisaiwa.tspi.nas.domain.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 17:43
 * @Version：1.0
 */
@Data
public class ResourcesEditReq {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 资源名称
     */
    @NotBlank
    private String resName;

    /**
     * 资源描述
     */
    private String resDesc;

    /**
     * 资源类型 0:FILE,1:SMB,2:FTP,3.WebDav
     */
    @NotNull
    private Integer resType;

    /**
     * 资源路径
     */
    private String resPath;

    /**
     * 使能状态:true使能,false禁用
     */
    @NotNull
    private Boolean enable;

}
