package com.github.gateway.support.response;

/**
 * Created by chdyan on 16/8/3.
 */
public class ResponseSuccessWrapper<T> extends ResponseWrapper {
    private T data;

    public ResponseSuccessWrapper(T data) {
        super(0, "success");
        this.data = data;
    }

    public ResponseSuccessWrapper(int status, String msg, T data) {
        super(status, msg);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
