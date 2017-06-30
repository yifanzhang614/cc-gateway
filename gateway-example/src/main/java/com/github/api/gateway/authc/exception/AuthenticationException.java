package com.github.api.gateway.authc.exception;

import com.github.api.gateway.GatewayRuntimeException;

/**
 * 认证异常
 * Created by chdyan on 16/8/2.
 */
public class AuthenticationException extends GatewayRuntimeException {

    public AuthenticationException() {
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}
