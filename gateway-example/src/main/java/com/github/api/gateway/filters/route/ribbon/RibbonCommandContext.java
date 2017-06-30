package com.github.api.gateway.filters.route.ribbon;

import org.springframework.util.MultiValueMap;

import java.io.InputStream;

/**
 * Created by yifanzhang.
 */
public class RibbonCommandContext {
    private final String serviceId;
    private final String verb;
    private final String uri;
    private final Boolean retryable;
    private final MultiValueMap<String, String> headers;
    private final MultiValueMap<String, String> params;
    private final InputStream requestEntity;

    public RibbonCommandContext(String serviceId, String verb, String uri, Boolean retryable,
        MultiValueMap<String, String> headers, MultiValueMap<String, String> params,
        InputStream requestEntity) {
        this.serviceId = serviceId;
        this.verb = verb;
        this.uri = uri;
        this.retryable = retryable;
        this.headers = headers;
        this.params = params;
        this.requestEntity = requestEntity;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getVerb() {
        return verb;
    }

    public String getUri() {
        return uri;
    }

    public Boolean getRetryable() {
        return retryable;
    }

    public MultiValueMap<String, String> getHeaders() {
        return headers;
    }

    public MultiValueMap<String, String> getParams() {
        return params;
    }

    public InputStream getRequestEntity() {
        return requestEntity;
    }
}
