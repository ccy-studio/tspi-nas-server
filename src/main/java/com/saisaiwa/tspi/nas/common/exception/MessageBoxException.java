package com.saisaiwa.tspi.nas.common.exception;

import lombok.Getter;

/**
 * @description:
 * @date: 2024/03/18 10:38
 * @author: saisiawa
 **/
@Getter
public class MessageBoxException extends RuntimeException {

    private final String msg;

    private final String title;

    private final int code;

    public MessageBoxException(String msg, String title, int code) {
        super(msg);
        this.msg = msg;
        this.title = title;
        this.code = code;
    }

    public MessageBoxException(String msg) {
        this.msg = msg;
        this.title = "提示";
        this.code = 500;
    }
}
