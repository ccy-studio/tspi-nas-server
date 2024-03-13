package com.saisaiwa.tspi.nas.domain.file;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @date: 2024/03/13 20:34
 * @author: saisiawa
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class FObjectRename extends FBaseEntity{

    @NotBlank
    private String newName;

}
