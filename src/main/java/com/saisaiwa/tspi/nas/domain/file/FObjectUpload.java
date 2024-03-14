package com.saisaiwa.tspi.nas.domain.file;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @date: 2024/03/14 09:46
 * @author: saisiawa
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class FObjectUpload extends FBaseEntity {

    /**
     * 上传的目标文件
     */
    @NotNull
    private Long targetFolder;

    /**
     * 文件名称
     */
    @NotBlank
    private String fileName;

    /**
     * 是否覆盖
     */
    @NotNull
    private Boolean isOverwrite;
}
