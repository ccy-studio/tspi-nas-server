package com.saisaiwa.tspi.nas.domain.file;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @date: 2024/03/13 16:45
 * @author: saisiawa
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class FObjectHas extends FBaseEntity {

    /**
     * 目标对象文件夹
     */
    @NotNull
    private Long targetObject;

    @NotBlank
    private String fileName;

}
