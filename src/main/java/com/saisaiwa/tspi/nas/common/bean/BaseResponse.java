package com.saisaiwa.tspi.nas.common.bean;

import com.saisaiwa.tspi.nas.common.enums.RespCode;
import com.saisaiwa.tspi.nas.common.util.NanoIdUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Base返回对象
 *
 * @param <T>
 * @author saisaiwa
 */
@Setter
@Getter
@ToString
public class BaseResponse<T> {
    private int code;
    private String msg;
    private T data;
    private final String traceId;

    public static <T> BaseResponse<T> ok() {
        return ok(null);
    }

    public static <T> BaseResponse<T> ok(T data) {
        BaseResponse<T> rsp = new BaseResponse<>();
        rsp.code = RespCode.SUCCESS.getCode();
        rsp.msg = RespCode.SUCCESS.getMsg();
        rsp.data = data;
        return rsp;
    }

    public static <T> BaseResponse<T> fail() {
        return fail(RespCode.ERROR, null);
    }

    public static <T> BaseResponse<T> fail(RespCode err) {
        return fail(err, null);
    }

    public static <T> BaseResponse<T> fail(int code, String msg) {
        return fail(code, msg, null);
    }

    public static <T> BaseResponse<T> fail(RespCode respCode, T data) {
        BaseResponse<T> rsp = new BaseResponse<>();
        rsp.code = respCode.getCode();
        rsp.msg = respCode.getMsg();
        rsp.data = data;
        return rsp;
    }

    public static <T> BaseResponse<T> fail(int code, String msg, T data) {
        BaseResponse<T> rsp = new BaseResponse<>();
        rsp.code = code;
        rsp.msg = msg;
        rsp.data = data;
        return rsp;
    }

    private BaseResponse() {
        this.traceId = NanoIdUtil.requestTraceId();
    }
}
