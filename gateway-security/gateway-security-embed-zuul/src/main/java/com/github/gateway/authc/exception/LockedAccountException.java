package com.github.gateway.authc.exception;

/**
 * Created by chongdi.yang on 2016/8/4.
 */
public class LockedAccountException extends AccountException {
    public LockedAccountException() {
    }

    public LockedAccountException(String message) {
        super(message);
    }

    public LockedAccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockedAccountException(Throwable cause) {
        super(cause);
    }
}
