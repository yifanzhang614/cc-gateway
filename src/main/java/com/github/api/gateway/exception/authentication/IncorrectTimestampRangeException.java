package com.github.api.gateway.exception.authentication;

/**
 * Created by chdyan on 16/8/3.
 */
public class IncorrectTimestampRangeException extends HeaderAuthenticationException {
    public IncorrectTimestampRangeException() {
    }

    public IncorrectTimestampRangeException(String message) {
        super(message);
    }

    public IncorrectTimestampRangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectTimestampRangeException(Throwable cause) {
        super(cause);
    }
}
