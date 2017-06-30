package com.github.api.gateway.filters.route.ribbon.util;

import com.github.api.gateway.archaius.ArchaiusAutoConfiguration;
import com.netflix.client.DefaultLoadBalancerRetryHandler;
import com.netflix.niws.client.http.RestClient;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.junit.Assert.assertEquals;

/**
 * Created by yifanzhang.
 */
public class SpringClientFactoryTests {
    private SpringClientFactory factory = new SpringClientFactory();

    @Test
    public void testConfigureRetry() {
        AnnotationConfigApplicationContext parent = new AnnotationConfigApplicationContext(
            ArchaiusAutoConfiguration.class);
        EnvironmentUtils.addEnvironment(parent, "foo.ribbon.MaxAutoRetries:2");
        this.factory.setApplicationContext(parent);
        DefaultLoadBalancerRetryHandler retryHandler = (DefaultLoadBalancerRetryHandler) this.factory
            .getLoadBalancerContext("foo").getRetryHandler();
        assertEquals(0, retryHandler.getMaxRetriesOnSameServer());
        parent.close();
        this.factory.destroy();
    }

    @Test
    public void testCookiePolicy() {
        RestClient client = this.factory.getClient("foo", RestClient.class);
        ApacheHttpClient4 jerseyClient = (ApacheHttpClient4) client.getJerseyClient();
        assertEquals(CookiePolicy.IGNORE_COOKIES, jerseyClient.getClientHandler()
            .getHttpClient().getParams().getParameter(ClientPNames.COOKIE_POLICY));
        this.factory.destroy();
    }
}
