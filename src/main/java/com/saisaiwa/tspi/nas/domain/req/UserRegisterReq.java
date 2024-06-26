package com.saisaiwa.tspi.nas.domain.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

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
    @NotBlank(message = "帐号不可为空")
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
    @NotBlank(message = "请输入密码")
    @Length(min = 6, message = "至少六位")
    private String password;

    /**
     * 用户组ID
     */
    @NotNull(message = "用户组不可为空")
    @NotEmpty
    private List<Long> userGroupIds;
}
