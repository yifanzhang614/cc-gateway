package com.github.api.gateway.exception;

/**
 * Created by chdyan on 16/8/3.
 */
public class GatewayRuntimeException extends RuntimeException {

    public GatewayRuntimeException() {
    }

    public GatewayRuntimeException(String message) {
        super(message);
    }

    public GatewayRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public GatewayRuntimeException(Throwable cause) {
        super(cause);
    }

}
