package com.github.api.gateway.support.response;

/**
 * Response Wrapper
 */
public class ResponseWrapper {
    private int status;
    private String msg;


    public ResponseWrapper(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static <T> ResponseWrapper error(ResponseErrorConstant error) {
        return new ResponseErrorWrapper(error);
    }

    public static <T> ResponseWrapper success(T data) {
        return new ResponseSuccessWrapper<T>(data);
    }
}
