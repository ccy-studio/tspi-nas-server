package com.saisaiwa.tspi.nas.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/7 14:56
 * @Version：1.0
 */
@SuppressWarnings("all")
@AllArgsConstructor
@Getter
public enum UserEnum {

    ADMIN(0, "admin");

    private int code;
    private String msg;

}
