package com.github.api.gateway.authc.exception;

/**
 * Created by chongdi.yang on 2016/8/4.
 */
public class CredentialsException extends AuthenticationException {
    public CredentialsException() {
    }

    public CredentialsException(String message) {
        super(message);
    }

    public CredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CredentialsException(Throwable cause) {
        super(cause);
    }
}
