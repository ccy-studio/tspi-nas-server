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
 * 存储桶
 * </p>
 *
 * @author Saisaiwa
 * @since 2024-03-08
 */
@Getter
@Setter
@TableName("t_buckets")
public class Buckets {

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 桶名称
     */
    @TableField("buckets_name")
    private String bucketsName;

    /**
     * 挂载点路径
     */
    @TableField("mount_point")
    private String mountPoint;

    /**
     * 资源ID
     */
    @TableField("res_id")
    private Long resId;

    /**
     * 权限:0私有,1公读公写,2公读私写
     */
    @TableField("permissions")
    private Integer permissions;

    /**
     * 权限范围:0私有,1资源内公开,2全公开,
     */
    @TableField("permissions_scope")
    private Integer permissionsScope;

    /**
     * 是否是静态页面
     */
    @TableField("static_page")
    private Boolean staticPage;

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
