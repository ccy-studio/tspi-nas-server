package com.saisaiwa.tspi.nas.domain.vo;

import com.saisaiwa.tspi.nas.common.file.FileBlockEntity;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @date: 2024/03/14 13:41
 * @author: saisiawa
 **/
@Data
public class FileBlockInfoVo {

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * MD5值
     */
    private String fileMd5;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 分块总数
     */
    private Integer blockCount;

    /**
     * 是否覆盖
     */
    private Boolean isOverwrite;

    /**
     * 当前完成的块
     */
    private List<FileBlockEntity> currentBlocks;
}
