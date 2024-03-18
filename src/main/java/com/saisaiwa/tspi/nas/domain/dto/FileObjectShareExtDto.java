package com.saisaiwa.tspi.nas.domain.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @description:
 * @date: 2024/03/18 14:20
 * @author: saisiawa
 **/
@Setter
@Getter
public class FileObjectShareExtDto {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 桶名
     */
    private String bucketsName;

    /**
     * 文件大小
     */
    private long fileSize;

}
