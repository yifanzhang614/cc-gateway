package com.github.api.gateway.filters.route.ribbon.client;

import com.github.api.gateway.filters.route.ribbon.RibbonCommandContext;
import com.github.api.gateway.filters.route.ribbon.RibbonCommandFactory;
import com.github.api.gateway.filters.route.ribbon.util.SpringClientFactory;
import com.netflix.client.http.HttpRequest;
import com.netflix.niws.client.http.RestClient;

import java.net.URISyntaxException;

/**
 * Created by yifanzhang.
 */
public class RestClientRibbonCommandFactory implements
    RibbonCommandFactory<RestClientRibbonCommand> {

    private final SpringClientFactory clientFactory;

    public RestClientRibbonCommandFactory(SpringClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    @Override
    public RestClientRibbonCommand create(RibbonCommandContext context)  {
        RestClient restClient = this.clientFactory.getClient(context.getServiceId(),
            RestClient.class);

        RestClientRibbonCommand command = null;
        try {
            command = new RestClientRibbonCommand(
                    context.getServiceId(), restClient, getVerb(context.getVerb()),
                    context.getUri(), context.getRetryable(), context.getHeaders(),
                    context.getParams(), context.getRequestEntity());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return command;
    }

    protected SpringClientFactory getClientFactory() {
        return this.clientFactory;
    }

    protected static HttpRequest.Verb getVerb(String sMethod) {
        if (sMethod == null)
            return HttpRequest.Verb.GET;
        try {
            return HttpRequest.Verb.valueOf(sMethod.toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return HttpRequest.Verb.GET;
        }
    }
}
