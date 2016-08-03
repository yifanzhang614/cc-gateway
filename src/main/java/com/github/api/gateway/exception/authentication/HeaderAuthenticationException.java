package com.github.api.gateway.exception.authentication;

/**
 * Created by chdyan on 16/8/3.
 */
public class HeaderAuthenticationException extends AuthenticationRuntimeException {
    public HeaderAuthenticationException() {
    }

    public HeaderAuthenticationException(String message) {
        super(message);
    }

    public HeaderAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public HeaderAuthenticationException(Throwable cause) {
        super(cause);
    }
}
