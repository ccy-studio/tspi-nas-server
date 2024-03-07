package com.saisaiwa.tspi.nas.domain.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 15:18
 * @Version：1.0
 */
@Data
public class UserUpdateReq {

    @NotNull
    private Long id;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 手机号
     */
    private String mobile;


    /**
     * 用户组ID
     */
    @NotNull(message = "用户组不可为空")
    @NotEmpty
    private List<Long> userGroupIds;
}
