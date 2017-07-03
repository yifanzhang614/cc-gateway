package com.github.gateway.support.response;

/**
 * Created by chdyan on 16/8/3.
 */
public enum ResponseErrorConstant {
    TIMESTAMP_EXPIRED(1, 100003, "请求时间戳失效"),
    UNKNOWN_APPID(1, 100004, "未知的APPID"),
    INCORRECT_SIGNATURE(1, 100005, "错误的签名"),
    UNKNOWN_ACCOUNT(1, 100006, "未知的账户"),
    INCORRECT_CREDENTIAL(1, 100007, "错误的凭证"),
    AUTHENTICATE_FAIL(1, 100008, "认证失败"),
    ;
    private int status;
    private int code;
    private String msg;

    ResponseErrorConstant(int status, int code, String msg) {
        this.status = status;
        this.code = code;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
