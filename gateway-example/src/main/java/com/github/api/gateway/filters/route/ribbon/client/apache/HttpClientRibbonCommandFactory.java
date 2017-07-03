package com.github.api.gateway.filters.route.ribbon.client.apache;

import com.github.api.gateway.filters.route.ribbon.RibbonCommandContext;
import com.github.api.gateway.filters.route.ribbon.RibbonCommandFactory;
import com.github.api.gateway.filters.route.ribbon.util.SpringClientFactory;

/**
 * Created by yifanzhang.
 */
public class HttpClientRibbonCommandFactory implements
    RibbonCommandFactory<HttpClientRibbonCommand> {
    private final SpringClientFactory clientFactory;

    public HttpClientRibbonCommandFactory(SpringClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public HttpClientRibbonCommand create(final RibbonCommandContext context) {
        final String serviceId = context.getServiceId();
        final RibbonLoadBalancingHttpClient client = this.clientFactory.getClient(
            serviceId, RibbonLoadBalancingHttpClient.class);
        client.setLoadBalancer(this.clientFactory.getLoadBalancer(serviceId));

        final HttpClientRibbonCommand httpClientRibbonCommand = new HttpClientRibbonCommand(
            serviceId, client, context.getVerb(), context.getUri(),
            context.getHeaders(), context.getParams(), context.getRequestEntity(),
            context.getRetryable());
        return httpClientRibbonCommand;
    }
}
