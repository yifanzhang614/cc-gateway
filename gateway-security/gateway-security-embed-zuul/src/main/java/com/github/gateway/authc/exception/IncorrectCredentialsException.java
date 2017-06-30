package com.github.gateway.authc.exception;

/**
 * Created by chdyan on 16/8/3.
 */
public class IncorrectCredentialsException extends CredentialsException {
    public IncorrectCredentialsException() {
    }

    public IncorrectCredentialsException(String message) {
        super(message);
    }

    public IncorrectCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectCredentialsException(Throwable cause) {
        super(cause);
    }
}
