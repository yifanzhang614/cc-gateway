package com.github.api.gateway.support.response;

/**
 * Created by chdyan on 16/8/3.
 */
public class ResponseErrorWrapper extends ResponseWrapper {
    private int error_code;

    public ResponseErrorWrapper(ResponseErrorConstant error) {
        super(error.getStatus(), error.getMsg());
        this.error_code = error.getCode();
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }
}
