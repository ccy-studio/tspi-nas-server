package com.saisaiwa.tspi.nas.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作:get_obj,put_obj,del_obj,share_obj,super
 *
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/8 11:12
 * @Version：1.0
 */
@AllArgsConstructor
@Getter
@SuppressWarnings("all")
public enum BucketsActionEnum {

    GET_OBJ("get_obj"),
    PUT_OBJ("put_obj"),
    DEL_OBJ("del_obj"),
    SHARE_OBJ("share_obj"),
    SUPER_AUTH("super");

    private final String premiss;

}
