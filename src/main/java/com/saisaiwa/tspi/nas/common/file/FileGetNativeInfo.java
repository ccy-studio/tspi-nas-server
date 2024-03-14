package com.saisaiwa.tspi.nas.common.file;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description:
 * @date: 2024/03/14 17:21
 * @author: saisiawa
 **/
@Data
@AllArgsConstructor
public class FileGetNativeInfo {

    private FileRangeInputStream inputStream;

    private long contentSize;

    private long start;

    private long end;

    private long fileSize;

}
