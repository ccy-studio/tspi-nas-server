package com.saisaiwa.tspi.nas.common.exception;

import com.saisaiwa.tspi.nas.common.enums.RespCode;
import lombok.Getter;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2024/3/1 16:37
 * @Version：1.0
 */
@Getter
public class BizException extends RuntimeException {

    private final String msg;

    private final int code;

    public BizException(RespCode respCode) {
        super(respCode.getMsg());
        this.msg = respCode.getMsg();
        this.code = respCode.getCode();
    }

    public BizException(String msg) {
        super(msg);
        this.msg = msg;
        this.code = RespCode.PROMPT.getCode();
    }

    public BizException(RespCode respCode, String msg) {
        super(msg);
        this.code = respCode.getCode();
        this.msg = msg;
    }
}
