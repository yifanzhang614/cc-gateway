package com.github.api.gateway.filters.route.ribbon.apache;

import com.github.api.gateway.filters.route.ribbon.client.apache.RibbonApacheHttpResponse;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;

/**
 * Created by yifanzhang.
 */
public class RibbonApacheHttpResponseTests {

    @Test
    public void testNullEntity() throws Exception {
        StatusLine statusLine = mock(StatusLine.class);
        given(statusLine.getStatusCode()).willReturn(204);
        HttpResponse response = mock(HttpResponse.class);
        given(response.getStatusLine()).willReturn(statusLine);

        RibbonApacheHttpResponse httpResponse = new RibbonApacheHttpResponse(response, URI
            .create("http://example.com"));

        assertThat(httpResponse.isSuccess(), is(true));
        assertThat(httpResponse.hasPayload(), is(false));
        assertThat(httpResponse.getPayload(), is(nullValue()));
        assertThat(httpResponse.getInputStream(), is(nullValue()));
    }


    @Test
    public void testNotNullEntity() throws Exception {
        StatusLine statusLine = mock(StatusLine.class);
        given(statusLine.getStatusCode()).willReturn(204);
        HttpResponse response = mock(HttpResponse.class);
        given(response.getStatusLine()).willReturn(statusLine);
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream(new byte[0]));
        given(response.getEntity()).willReturn(entity);

        RibbonApacheHttpResponse httpResponse = new RibbonApacheHttpResponse(response, URI.create("http://example.com"));

        assertThat(httpResponse.isSuccess(), is(true));
        assertThat(httpResponse.hasPayload(), is(true));
        assertThat(httpResponse.getPayload(), is(notNullValue()));
        assertThat(httpResponse.getInputStream(), is(notNullValue()));
    }
}
