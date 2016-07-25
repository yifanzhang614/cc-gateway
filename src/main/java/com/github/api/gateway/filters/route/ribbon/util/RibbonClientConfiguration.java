package com.github.api.gateway.filters.route.ribbon.util;

import com.netflix.client.DefaultLoadBalancerRetryHandler;
import com.netflix.client.RetryHandler;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import com.netflix.niws.client.http.RestClient;
import com.netflix.servo.monitor.Monitors;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.client.apache4.ApacheHttpClient4;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * This class defines the default implementations of a ribbon client to use.
 * These default implementations include:
 *  - IClientConfig ribbonClientConfig: DefaultClientConfigImpl
 *  - IRule ribbonRule: ZoneAvoidanceRule
 *  - IPing ribbonPing: NoOpPing
 *  - ServerList<Server> ribbonServerList: ConfigurationBasedServerList
 *  - ServerListFilter<Server> ribbonServerListFilter: ZonePreferenceServerListFilter
 *  - ILoadBalancer ribbonLoadBalancer: ZoneAwareLoadBalancer
 *
 *  You can define another ribbon client configuration(inherit this one or a complete new one)
 * and init it in the SpringClientFactory.
 *
 * Created by yifanzhang.
 */
@Configuration
public class RibbonClientConfiguration {

//    @Value("${ribbon.client.name}")
    private String name = "cc-client";
//    private final DynamicStringProperty name = DynamicPropertyFactory.getInstance()
//        .getStringProperty("ribbon.client.name", "cc-client");

    // TODO: maybe re-instate autowired load balancers: identified
    // by name they could be associated with ribbon clients

//    @Bean @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean
    public IClientConfig ribbonClientConfig() {
        DefaultClientConfigImpl config = new DefaultClientConfigImpl();
//        config.loadProperties(this.name.get());
//        config.loadProperties(this.name);
        return config;
    }

    @Bean
    public IRule ribbonRule(IClientConfig config) {
        ZoneAvoidanceRule rule = new ZoneAvoidanceRule();
        rule.initWithNiwsConfig(config);
        return rule;
    }

    @Bean
    public IPing ribbonPing(IClientConfig config) {
        // TODO: use PingUrl
        return new NoOpPing();
    }

    @Bean
    public ServerList<Server> ribbonServerList(IClientConfig config) {
        ConfigurationBasedServerList serverList = new ConfigurationBasedServerList();
        serverList.initWithNiwsConfig(config);
        return serverList;
    }

    /**
     * Create a Netflix {@link RestClient} integrated with Ribbon if none already exists
     * in the application context. It is not required for Ribbon to work properly and is
     * therefore created lazily if ever another component requires it.
     *
     * @param config             the configuration to use by the underlying Ribbon instance
     * @param loadBalancer       the load balancer to use by the underlying Ribbon instance
     * @param serverIntrospector server introspector to use by the underlying Ribbon instance
     * @param retryHandler       retry handler to use by the underlying Ribbon instance
     * @return a {@link RestClient} instances backed by Ribbon
     */
    @Bean @Lazy
    public RestClient ribbonRestClient(IClientConfig config, ILoadBalancer loadBalancer,
        ServerIntrospector serverIntrospector, RetryHandler retryHandler) {
        RestClient client = new OverrideRestClient(config, serverIntrospector);
        client.setLoadBalancer(loadBalancer);
        client.setRetryHandler(retryHandler);
        Monitors.registerObject("Client_" + this.name, client);
        return client;
    }

    @Bean
    public ILoadBalancer ribbonLoadBalancer(IClientConfig config, ServerList<Server> serverList,
        ServerListFilter<Server> serverListFilter, IRule rule, IPing ping) {
        ZoneAwareLoadBalancer<Server> balancer =
            LoadBalancerBuilder.newBuilder().withClientConfig(config).withRule(rule).withPing(ping)
                .withServerListFilter(serverListFilter).withDynamicServerList(serverList)
                .buildDynamicServerListLoadBalancer();
        return balancer;
    }

    @Bean
    public ServerListFilter<Server> ribbonServerListFilter(IClientConfig config) {
        ZonePreferenceServerListFilter filter = new ZonePreferenceServerListFilter();
        filter.initWithNiwsConfig(config);
        return filter;
    }

    @Bean
    public RibbonLoadBalancerContext ribbonLoadBalancerContext(ILoadBalancer loadBalancer,
        IClientConfig config, RetryHandler retryHandler) {
        return new RibbonLoadBalancerContext(loadBalancer, config, retryHandler);
    }

    @Bean
    public RetryHandler retryHandler(IClientConfig config) {
        return new DefaultLoadBalancerRetryHandler(config);
    }

    @Bean
    public ServerIntrospector serverIntrospector() {
        return new DefaultServerIntrospector();
    }

//    @PostConstruct public void preprocess() {
//        RibbonProperyUtils.setRibbonProperty(name, DeploymentContextBasedVipAddresses.key(), name);
//    }

    static class OverrideRestClient extends RestClient {

        private ServerIntrospector serverIntrospector;

        protected OverrideRestClient(IClientConfig ncc, ServerIntrospector serverIntrospector) {
            super();
            this.serverIntrospector = serverIntrospector;
            initWithNiwsConfig(ncc);
        }

        @Override public URI reconstructURIWithServer(Server server, URI original) {
            String scheme = original.getScheme();
            if (!"https".equals(scheme) && this.serverIntrospector.isSecure(server)) {
                original = UriComponentsBuilder.fromUri(original).scheme("https").build().toUri();
            }
            return super.reconstructURIWithServer(server, original);
        }

        @Override protected Client apacheHttpClientSpecificInitialization() {
            ApacheHttpClient4 apache =
                (ApacheHttpClient4) super.apacheHttpClientSpecificInitialization();
            apache.getClientHandler().getHttpClient().getParams()
                .setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.IGNORE_COOKIES);
            return apache;
        }

    }
}
