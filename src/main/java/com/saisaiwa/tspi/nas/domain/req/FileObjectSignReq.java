package com.saisaiwa.tspi.nas.domain.req;

import com.saisaiwa.tspi.nas.domain.file.FBaseEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @date: 2024/03/26 09:54
 * @author: saisiawa
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class FileObjectSignReq extends FBaseEntity {

    @NotBlank
    private String uuid;

    /**
     * 类型上传为1，下载为0
     */
    private int type;

}
