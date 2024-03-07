package com.saisaiwa.tspi.nas.domain.dto;

import com.saisaiwa.tspi.nas.domain.entity.User;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 10:54
 * @Version：1.0
 */
@Setter
@Getter
public class UserExtDto extends User {

    /**
     * 组名称，多个逗号分割
     */
    private String groupName;
}
