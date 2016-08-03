package com.github.api.gateway.exception.authentication;

/**
 * Created by chdyan on 16/8/3.
 */
public class UnknownAppIdException extends HeaderAuthenticationException {
    public UnknownAppIdException() {
    }

    public UnknownAppIdException(String message) {
        super(message);
    }

    public UnknownAppIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownAppIdException(Throwable cause) {
        super(cause);
    }
}
