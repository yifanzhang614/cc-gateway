package com.github.api.gateway.filters.route.ribbon.client.apache;

import com.netflix.client.ClientRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

/**
 * Created by yifanzhang.
 */
public class RibbonApacheHttpRequest extends ClientRequest implements Cloneable {
    private final String method;

    private final MultiValueMap<String, String> headers;

    private final MultiValueMap<String, String> params;

    private final InputStream requestEntity;

    public RibbonApacheHttpRequest(final String method, final URI uri,
        final Boolean retryable, final MultiValueMap<String, String> headers,
        final MultiValueMap<String, String> params, final InputStream requestEntity) {

        this.method = method;
        this.uri = uri;
        this.isRetriable = retryable;
        this.headers = headers;
        this.params = params;
        this.requestEntity = requestEntity;
    }

    public HttpUriRequest toRequest(final RequestConfig requestConfig) {
        final RequestBuilder builder = RequestBuilder.create(this.method);
        builder.setUri(this.uri);
        for (final String name : this.headers.keySet()) {
            final List<String> values = this.headers.get(name);
            for (final String value : values) {
                builder.addHeader(name, value);
            }
        }

        for (final String name : this.params.keySet()) {
            final List<String> values = this.params.get(name);
            for (final String value : values) {
                builder.addParameter(name, value);
            }
        }

        if (this.requestEntity != null) {
            final BasicHttpEntity entity;
            entity = new BasicHttpEntity();
            entity.setContent(this.requestEntity);
            builder.setEntity(entity);
        }

        builder.setConfig(requestConfig);
        return builder.build();
    }

    public RibbonApacheHttpRequest withNewUri(final URI uri) {
        return new RibbonApacheHttpRequest(this.method, uri, this.isRetriable,
            this.headers, this.params, this.requestEntity);
    }

    public String getMethod() {
        return method;
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
