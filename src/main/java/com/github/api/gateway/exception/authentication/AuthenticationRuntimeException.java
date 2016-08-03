package com.github.api.gateway.exception.authentication;

import com.github.api.gateway.exception.GatewayRuntimeException;
import com.netflix.zuul.exception.ZuulException;

/**
 * 认证异常
 * Created by chdyan on 16/8/2.
 */
public class AuthenticationRuntimeException extends GatewayRuntimeException {

    public AuthenticationRuntimeException() {
    }

    public AuthenticationRuntimeException(String message) {
        super(message);
    }

    public AuthenticationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationRuntimeException(Throwable cause) {
        super(cause);
    }
}
