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

    private RespCode respCode = RespCode.PROMPT;

    public BizException(RespCode respCode) {
        super(respCode.getMsg());
        this.respCode = respCode;
    }

    public BizException(String msg) {
        super(msg);
    }

    public BizException(RespCode respCode, String msg) {
        super(msg);
        this.respCode = respCode;
    }
}
