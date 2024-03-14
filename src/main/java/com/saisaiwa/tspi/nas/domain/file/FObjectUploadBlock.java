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
public class FObjectUploadBlock extends FObjectUpload {

    /**
     * md5
     */
    @NotBlank
    private String md5;

    /**
     * 文件数量
     */
    @NotNull
    private Integer fileCount;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * record的ID
     */
    private Long blockId;
}
