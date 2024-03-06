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
 * 存储资源
 * </p>
 *
 * @author Saisaiwa
 * @since 2024-03-06
 */
@Getter
@Setter
@TableName("t_resources")
public class Resources {

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资源名称
     */
    @TableField("res_name")
    private String resName;

    /**
     * 资源描述
     */
    @TableField("res_desc")
    private String resDesc;

    /**
     * 资源类型 0:FILE,1:SMB,2:FTP,3.WebDav
     */
    @TableField("res_type")
    private Integer resType;

    /**
     * 资源路径
     */
    @TableField("res_path")
    private String resPath;

    /**
     * 使能状态:true使能,false禁用
     */
    @TableField("enable")
    private Boolean enable;

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
