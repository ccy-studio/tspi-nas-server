package com.saisaiwa.tspi.nas.common.bean;

import jakarta.validation.constraints.NotBlank;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2023/9/12 16:26
 * @Version：1.0
 */
public class StrReq extends BaseRequest {

    @NotBlank
    private String val;

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}
