package com.github.api.gateway.authc.exception;

/**
 * Created by chdyan on 16/8/3.
 */
public class IncorrectSignatureException extends CredentialsException {
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
