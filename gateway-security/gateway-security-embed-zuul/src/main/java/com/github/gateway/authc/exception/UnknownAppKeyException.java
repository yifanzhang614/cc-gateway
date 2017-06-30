package com.github.gateway.authc.exception;

/**
 * Created by chdyan on 16/8/3.
 */
public class UnknownAppKeyException extends AccountException {
    public UnknownAppKeyException() {
    }

    public UnknownAppKeyException(String message) {
        super(message);
    }

    public UnknownAppKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownAppKeyException(Throwable cause) {
        super(cause);
    }
}
