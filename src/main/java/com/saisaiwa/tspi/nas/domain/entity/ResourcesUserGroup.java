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
 * 存储资源关联用户组
 * </p>
 *
 * @author Saisaiwa
 * @since 2024-03-06
 */
@Getter
@Setter
@TableName("t_resources_user_group")
public class ResourcesUserGroup {

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资源ID
     */
    @TableField("res_id")
    private Long resId;

    /**
     * 用户组ID
     */
    @TableField("user_group_id")
    private Long userGroupId;

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
