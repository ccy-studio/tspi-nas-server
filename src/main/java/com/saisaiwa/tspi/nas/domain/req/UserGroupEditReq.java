package com.saisaiwa.tspi.nas.domain.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 15:40
 * @Version：1.0
 */
@Data
public class UserGroupEditReq {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 组名称
     */
    @NotBlank
    private String groupName;

    /**
     * 资源ID列表
     */
    @NotNull
    @NotEmpty
    private List<Long> resIds;
}
