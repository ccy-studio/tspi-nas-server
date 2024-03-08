package com.saisaiwa.tspi.nas.domain.vo;

import lombok.Data;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 15:53
 * @Version：1.0
 */
@Data
public class UserGroupListVo {

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 组名称
     */
    private String groupName;

    /**
     * 绑定用户数量
     */
    private Integer bindUserCount;

    /**
     * 绑定资源数量
     */
    private Integer bindResCount;
}
