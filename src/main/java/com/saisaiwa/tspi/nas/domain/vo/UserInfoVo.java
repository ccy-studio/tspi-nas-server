package com.saisaiwa.tspi.nas.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/6 19:35
 * @Version：1.0
 */
@Data
public class UserInfoVo {
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 用户账户
     */
    private String userAccount;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * AK
     */
    private String accessKey;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 用户组名称
     */
    private List<Long> userGroupIds;
}
