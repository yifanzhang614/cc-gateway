package com.github.api.gateway.authc.exception;

/**
 * Created by chdyan on 16/8/3.
 */
public class ExpiredTimestampException extends AuthenticationException {
    public ExpiredTimestampException() {
    }

    public ExpiredTimestampException(String message) {
        super(message);
    }

    public ExpiredTimestampException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpiredTimestampException(Throwable cause) {
        super(cause);
    }
}
