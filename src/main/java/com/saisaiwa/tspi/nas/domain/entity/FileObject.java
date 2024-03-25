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
 * 文件对象
 * </p>
 *
 * @author Saisaiwa
 * @since 2024-03-06
 */
@Getter
@Setter
@TableName("t_file_object")
public class FileObject {

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 桶ID
     */
    @TableField("buckets_id")
    private Long bucketsId;

    /**
     * 文件对象名称
     */
    @TableField("file_name")
    private String fileName;

    /**
     * 文件后缀类型
     */
    @TableField("file_content_type")
    private String fileContentType;

    /**
     * 文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 真实物理路径
     */
    @TableField("real_path")
    private String realPath;

    /**
     * 文件大小
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * MD5值
     */
    @TableField("file_md5")
    private String fileMd5;

    /**
     * 是否是目录
     */
    @TableField("is_dir")
    private Boolean isDir;

    /**
     * 父目录ID,无则为空
     */
    @TableField("parent_id")
    private Long parentId;

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

    /**
     * 临时ID
     */
    @TableField(exist = false)
    private Long tempUid;
}
