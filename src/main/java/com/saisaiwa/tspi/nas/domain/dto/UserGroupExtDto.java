package com.saisaiwa.tspi.nas.domain.dto;

import com.saisaiwa.tspi.nas.domain.entity.UserGroup;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 15:54
 * @Version：1.0
 */
@Getter
@Setter
public class UserGroupExtDto extends UserGroup {

    /**
     * 绑定用户数量
     */
    private int bindUserCount;

    /**
     * 绑定资源数量
     */
    private int bindResCount;

}
