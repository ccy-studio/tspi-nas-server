package com.saisaiwa.tspi.nas.domain.file;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @date: 2024/03/13 16:45
 * @author: saisiawa
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class FObjectGet extends FBaseEntity {

    /**
     * 下载还是预览
     */
    private boolean isDownload;

}
