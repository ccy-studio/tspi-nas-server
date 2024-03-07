package com.saisaiwa.tspi.nas.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/1 16:30
 * @Version：1.0
 */
@SuppressWarnings("all")
@AllArgsConstructor
@Getter
public enum RespCode {

    SUCCESS(200, "ok"),
    PROMPT(0, "操作失败"),
    ERROR(500, "服务异常"),
    BAD_REQUEST(400, "bad request"),
    LOGIN_FAIL(4000, "用户名或密码错误"),
    UNAUTHORIZED(4001, "未授权"),
    SESSION_TIMEOUT(4002, "Session timeout"),
    INVALID_TOKEN(4003, "Invalid token"),
    INVALID_PARAMS(420, "Invalid parameters"),
    PERMISSION_DENIED(403, "Permission denied"),
    DATA_REFUSE(5002,"此数据拒绝操作");

    private int code;
    private String msg;
}
