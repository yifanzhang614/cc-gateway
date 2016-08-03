package com.github.api.gateway.exception.authentication;

/**
 * Created by chdyan on 16/8/3.
 */
public class IncorrectCredentialException extends AuthenticationRuntimeException {
    public IncorrectCredentialException() {
    }

    public IncorrectCredentialException(String message) {
        super(message);
    }

    public IncorrectCredentialException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectCredentialException(Throwable cause) {
        super(cause);
    }
}
