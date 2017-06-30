package com.github.gateway.authc.exception;

/**
 * Created by chongdi.yang on 2016/8/4.
 */
public class AccountException extends AuthenticationException {
    public AccountException() {
    }

    public AccountException(String message) {
        super(message);
    }

    public AccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountException(Throwable cause) {
        super(cause);
    }
}
