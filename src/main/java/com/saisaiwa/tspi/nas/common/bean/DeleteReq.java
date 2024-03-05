package com.saisaiwa.tspi.nas.common.bean;

public class DeleteReq<T> extends BaseRequest {
    private T id;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }
}
