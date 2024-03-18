package com.saisaiwa.tspi.nas.domain.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @description:
 * @date: 2024/03/18 13:49
 * @author: saisiawa
 **/
@Data
public class FileSharePasswordReq {

    @NotNull
    private String shareKey;

    @NotBlank
    private String password;

}
