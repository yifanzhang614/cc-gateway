package com.github.api.gateway.filters.route.ribbon.util;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by yifanzhang.
 */
public class RibbonClientConfigurationTests {

    @Test
    public void restClientInitCalledOnce() {
        CountingConfig config = new CountingConfig();
        config.setProperty(CommonClientConfigKey.ConnectTimeout, "1");
        config.setProperty(CommonClientConfigKey.ReadTimeout, "1");
        config.setProperty(CommonClientConfigKey.MaxHttpConnectionsPerHost, "1");
        config.setClientName("testClient");
        new TestRestClient(config);
        assertThat(config.count, is(equalTo(1)));
    }

    static class CountingConfig extends DefaultClientConfigImpl {
        int count = 0;
    }

    static class TestRestClient extends RibbonClientConfiguration.OverrideRestClient {

        private TestRestClient(IClientConfig ncc) {
            super(ncc, new DefaultServerIntrospector());
        }

        @Override
        public void initWithNiwsConfig(IClientConfig clientConfig) {
            ((CountingConfig) clientConfig).count++;
            super.initWithNiwsConfig(clientConfig);
        }
    }
}
