package com.github.api.gateway.filters.route.ribbon.apache;

import com.github.api.gateway.filters.route.ribbon.client.apache.RibbonApacheHttpRequest;
import com.github.api.gateway.filters.route.ribbon.client.apache.RibbonLoadBalancingHttpClient;
import com.github.api.gateway.filters.route.ribbon.util.SpringClientFactory;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.util.ReflectionTestUtils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by yifanzhang.
 */
public class RibbonLoadBalancingHttpClientTests {

    @Test
    public void testRequestConfigDoNotFollowRedirectsOverrideWithFollowRedirects()
        throws Exception {

        DefaultClientConfigImpl override = new DefaultClientConfigImpl();
        override.set(CommonClientConfigKey.FollowRedirects, true);
        override.set(CommonClientConfigKey.IsSecure, false);

        RequestConfig result = getBuiltRequestConfig(DoNotFollowRedirects.class, override);

        assertThat(result.isRedirectsEnabled(), is(true));
    }

    @Test
    public void testRequestConfigUseDefaultsNoOverride() throws Exception {
        RequestConfig result = getBuiltRequestConfig(UseDefaults.class, null);

        assertThat(result.isRedirectsEnabled(), is(false));
    }

    @Test
    public void testRequestConfigDoNotFollowRedirectsNoOverride() throws Exception {
        RequestConfig result = getBuiltRequestConfig(DoNotFollowRedirects.class, null);

        assertThat(result.isRedirectsEnabled(), is(false));
    }

    @Test
    public void testRequestConfigFollowRedirectsNoOverride() throws Exception {
        RequestConfig result = getBuiltRequestConfig(FollowRedirects.class, null);

        assertThat(result.isRedirectsEnabled(), is(true));
    }

    @Test
    public void testRequestConfigFollowRedirectsOverrideWithDoNotFollowRedirects()
        throws Exception {

        DefaultClientConfigImpl override = new DefaultClientConfigImpl();
        override.set(CommonClientConfigKey.FollowRedirects, false);
        override.set(CommonClientConfigKey.IsSecure, false);

        RequestConfig result = getBuiltRequestConfig(FollowRedirects.class, override);

        assertThat(result.isRedirectsEnabled(), is(false));
    }

    @Configuration
    protected static class UseDefaults {

    }

    @Configuration
    protected static class FollowRedirects {
        @Bean
        public IClientConfig clientConfig() {
            DefaultClientConfigImpl config = new DefaultClientConfigImpl();
            config.set(CommonClientConfigKey.FollowRedirects, true);
            return config;
        }
    }

    @Configuration
    protected static class DoNotFollowRedirects {
        @Bean
        public IClientConfig clientConfig() {
            DefaultClientConfigImpl config = new DefaultClientConfigImpl();
            config.set(CommonClientConfigKey.FollowRedirects, false);
            return config;
        }
    }

    private RequestConfig getBuiltRequestConfig(Class<?> defaultConfigurationClass,
        IClientConfig configOverride) throws Exception {

        SpringClientFactory factory = new SpringClientFactory();
        factory.setApplicationContext(new AnnotationConfigApplicationContext(
            defaultConfigurationClass));
        HttpClient delegate = mock(HttpClient.class);
        RibbonLoadBalancingHttpClient client = factory.getClient("service",
            RibbonLoadBalancingHttpClient.class);

        ReflectionTestUtils.setField(client, "delegate", delegate);
        given(delegate.execute(any(HttpUriRequest.class))).willReturn(mock(HttpResponse.class));
        RibbonApacheHttpRequest request = mock(RibbonApacheHttpRequest.class);
        given(request.toRequest(any(RequestConfig.class))).willReturn(
            mock(HttpUriRequest.class));

        client.execute(request, configOverride);

        ArgumentCaptor<RequestConfig> requestConfigCaptor = ArgumentCaptor
            .forClass(RequestConfig.class);
        verify(request).toRequest(requestConfigCaptor.capture());
        return requestConfigCaptor.getValue();
    }
}
