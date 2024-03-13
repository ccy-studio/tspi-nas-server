package com.saisaiwa.tspi.nas.domain.vo;

import lombok.Data;

/**
 * @description:
 * @date: 2024/03/13 16:20
 * @author: saisiawa
 **/
@Data
public class FileObjectInfoVo {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 文件对象名称
     */
    private String fileName;

    /**
     * 文件后缀类型
     */
    private String fileContentType;

    /**
     * 文件路径
     */
    private String filePath;


    /**
     * 文件大小
     */
    private Long fileSize;


    /**
     * 是否是目录
     */
    private Boolean isDir;


    /**
     * 父目录ID,无则为空
     */
    private Long parentId;


    /**
     * 创建时间
     */
    private String createTime;

}
