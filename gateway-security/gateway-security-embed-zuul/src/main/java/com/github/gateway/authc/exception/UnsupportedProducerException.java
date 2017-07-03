package com.github.gateway.authc.exception;

/**
 * Created by chongdi.yang on 2016/8/6.
 */
public class UnsupportedProducerException extends AuthenticationException {
    public UnsupportedProducerException() {
    }

    public UnsupportedProducerException(String message) {
        super(message);
    }

    public UnsupportedProducerException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedProducerException(Throwable cause) {
        super(cause);
    }
}
