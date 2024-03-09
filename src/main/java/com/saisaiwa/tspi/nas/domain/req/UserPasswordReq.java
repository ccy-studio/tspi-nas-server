package com.saisaiwa.tspi.nas.domain.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 15:27
 * @Version：1.0
 */
@Data
public class UserPasswordReq {

    private Long id;

    @NotBlank
    private String newPwd;

    @NotBlank
    @Length(min = 6)
    private String oldPwd;

}
