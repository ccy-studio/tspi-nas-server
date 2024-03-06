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
 * 文件对象分享
 * </p>
 *
 * @author Saisaiwa
 * @since 2024-03-06
 */
@Getter
@Setter
@TableName("t_file_object_share")
public class FileObjectShare {

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 对象ID
     */
    @TableField("file_object_id")
    private Long fileObjectId;

    /**
     * 分享签名KEY
     */
    @TableField("sign_key")
    private String signKey;

    /**
     * 过期时间,为空永久
     */
    @TableField("expiration_time")
    private LocalDateTime expirationTime;

    /**
     * 访问密码
     */
    @TableField("access_password")
    private String accessPassword;

    /**
     * 访问次数
     */
    @TableField("click_count")
    private Long clickCount;

    /**
     * 是否是直链
     */
    @TableField("is_symlink")
    private Boolean isSymlink;

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
