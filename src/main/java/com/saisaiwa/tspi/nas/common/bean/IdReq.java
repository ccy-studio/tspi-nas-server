package com.saisaiwa.tspi.nas.common.bean;

import jakarta.validation.constraints.NotNull;

/**
 * @Description:
 * @Author: Chen Ze Deng
 * @Date: 2023/6/21 11:13
 * @Version：1.0
 */
public class IdReq<T> extends BaseRequest {
    @NotNull
    private T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }
}
