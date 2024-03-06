package com.saisaiwa.tspi.nas.domain.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/6 19:46
 * @Version：1.0
 */
@Data
public class UserRegisterReq {

    /**
     * 用户账户
     */
    @NotBlank
    private String userAccount;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 密码
     */
    @NotBlank
    private String password;

    /**
     * 用户组ID
     */
    @NotNull
    private Long userGroupId;
}
