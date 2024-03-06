package com.saisaiwa.tspi.nas.domain.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/6 17:59
 * @Version：1.0
 */
@Data
public class LoginReq {

    @NotBlank(message = "账号不可为空")
    private String account;

    @NotBlank(message = "密码不可为空")
    private String password;

}
