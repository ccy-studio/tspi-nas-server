package com.saisaiwa.tspi.nas.common.file;

import lombok.Data;

/**
 * @description:
 * @date: 2024/03/12 14:20
 * @author: saisiawa
 **/
@Data
public class FileBlockEntity {

    private String fileName;

    private int number;

    private long size;

}
