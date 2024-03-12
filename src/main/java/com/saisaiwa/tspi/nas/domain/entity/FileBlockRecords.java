package com.saisaiwa.tspi.nas.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 文件分块续传记录表
 * </p>
 *
 * @author Saisaiwa
 * @since 2024-03-12
 */
@Getter
@Setter
@TableName("t_file_block_records")
public class FileBlockRecords {

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父文件对象id
     */
    @TableField("file_object_parent_id")
    private Long fileObjectParentId;

    /**
     * 文件名称
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 真实物理路径
     */
    @TableField("dir_path")
    private String dirPath;

    /**
     * MD5值
     */
    @TableField("file_md5")
    private String fileMd5;

    /**
     * 文件大小
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 分块总数
     */
    @TableField("block_count")
    private Integer blockCount;

    /**
     * 是否覆盖
     */
    @TableField("is_overwrite")
    private Boolean isOverwrite;

    /**
     * 是否已删除 0:未删除
     */
    @TableField("is_delete")
    private Long isDelete;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField("create_user")
    private Long createUser;

    /**
     * 修改人
     */
    @TableField("update_user")
    private Long updateUser;
}
