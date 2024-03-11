package com.saisaiwa.tspi.nas.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

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
public enum BucketsACLEnum {

    GET_OBJ("get_obj"),
    PUT_OBJ("put_obj"),
    DEL_OBJ("del_obj"),
    SHARE_OBJ("share_obj"),
    SUPER_AUTH("super");

    private final String premiss;

    /**
     * 返回操作权限字符描述的集合
     *
     * @param ems
     * @return
     */
    public static String toAction(BucketsACLEnum... ems) {
        return Arrays.stream(ems).map(BucketsACLEnum::getPremiss).collect(Collectors.joining(";"));
    }

    public boolean check(String acl) {
        return acl.contains(this.getPremiss());
    }
}
