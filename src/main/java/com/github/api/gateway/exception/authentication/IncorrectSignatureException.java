package com.github.api.gateway.exception.authentication;

/**
 * Created by chdyan on 16/8/3.
 */
public class IncorrectSignatureException extends HeaderAuthenticationException {
    public IncorrectSignatureException() {
    }

    public IncorrectSignatureException(String message) {
        super(message);
    }

    public IncorrectSignatureException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectSignatureException(Throwable cause) {
        super(cause);
    }
}
