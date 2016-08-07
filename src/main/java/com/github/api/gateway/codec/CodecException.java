package com.github.api.gateway.codec;

import com.github.api.gateway.GatewayRuntimeException;

/**
 * Created by chongdi.yang on 2016/8/5.
 */
public class CodecException extends GatewayRuntimeException {

    public CodecException() {
    }

    public CodecException(String message) {
        super(message);
    }

    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodecException(Throwable cause) {
        super(cause);
    }
}
