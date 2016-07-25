package com.github.api.gateway.filters.route.ribbon.client.apache;


import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.netflix.client.ClientException;
import com.netflix.client.http.CaseInsensitiveMultiMap;
import com.netflix.client.http.HttpHeaders;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.*;

/**
 * Created by yifanzhang.
 */
public class RibbonApacheHttpResponse implements com.netflix.client.http.HttpResponse{
    private HttpResponse httpResponse;
    private URI uri;

    public RibbonApacheHttpResponse(final HttpResponse httpResponse, final URI uri) {
        Assert.notNull(httpResponse, "httpResponse can not be null");
        this.httpResponse = httpResponse;
        this.uri = uri;
    }

    @Override
    public Object getPayload() throws ClientException {
        try {
            if (!hasPayload()) {
                return null;
            }
            return this.httpResponse.getEntity().getContent();
        }
        catch (final IOException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    @Override
    public boolean hasPayload() {
        return this.httpResponse.getEntity() != null;
    }

    @Override
    public boolean isSuccess() {
        return HttpStatus.valueOf(this.httpResponse.getStatusLine().getStatusCode()).is2xxSuccessful();
    }

    @Override
    public URI getRequestedURI() {
        return this.uri;
    }

    public int getStatus() {
        return httpResponse.getStatusLine().getStatusCode();
    }

    public String getStatusLine() {
        return httpResponse.getStatusLine().toString();
    }

    @Override
    public Map<String, Collection<String>> getHeaders() {
        final Map<String, Collection<String>> headers = Maps.newHashMap();
        for (final Header header : this.httpResponse.getAllHeaders()) {
            if (headers.containsKey(header.getName())) {
                headers.get(header.getName()).add(header.getValue());
            }
            else {
                final List<String> values = new ArrayList<String>();
                values.add(header.getValue());
                headers.put(header.getName(), values);
            }
        }

        return headers;
    }

    @Override
    public HttpHeaders getHttpHeaders() {
        final CaseInsensitiveMultiMap headers = new CaseInsensitiveMultiMap();
        for (final Header header : httpResponse.getAllHeaders()) {
            headers.addHeader(header.getName(), header.getValue());
        }

        return headers;
    }

    @Override
    public void close() {
        if (this.httpResponse != null && this.httpResponse.getEntity() != null) {
            try {
                this.httpResponse.getEntity().getContent().close();
            }
            catch (final IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

    }

    @Override
    public InputStream getInputStream() {
        try {
            if (!hasPayload()) {
                return null;
            }
            return this.httpResponse.getEntity().getContent();
        }
        catch (final IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public boolean hasEntity() {
        return hasPayload();
    }

    /**
     * Not used
     */
    @Override
    public <T> T getEntity(final Class<T> type) throws Exception {
        return null;
    }

    /**
     * Not used
     */
    @Override
    public <T> T getEntity(final Type type) throws Exception {
        return null;
    }

    /**
     * Not used
     */
    @Override
    public <T> T getEntity(final TypeToken<T> type) throws Exception {
        return null;
    }
}
