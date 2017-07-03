package com.github.gateway.authc.exception;

/**
 * Created by chongdi.yang on 2016/8/4.
 */
public class ExpiredCredentialsException extends CredentialsException {
    public ExpiredCredentialsException() {
    }

    public ExpiredCredentialsException(String message) {
        super(message);
    }

    public ExpiredCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpiredCredentialsException(Throwable cause) {
        super(cause);
    }
}
