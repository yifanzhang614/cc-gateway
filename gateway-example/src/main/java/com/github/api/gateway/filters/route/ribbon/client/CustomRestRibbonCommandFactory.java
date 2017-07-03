package com.github.api.gateway.filters.route.ribbon.client;

import com.github.api.gateway.filters.route.ribbon.RibbonCommandContext;
import com.github.api.gateway.filters.route.ribbon.util.SpringClientFactory;
import com.netflix.client.config.IClientConfig;
import com.netflix.niws.client.http.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;

/**
 * Set a rule to generate several Rest based Ribbon CommandFactory
 * Created by yifanzhang.
 */
public class CustomRestRibbonCommandFactory extends RestClientRibbonCommandFactory{

    private static final Logger log = LoggerFactory.getLogger(CustomRestRibbonCommandFactory.class);
    public CustomRestRibbonCommandFactory(SpringClientFactory clientFactory) {
        super(clientFactory);
    }

    public RestClientRibbonCommand create(RibbonCommandContext context) {
//        String uri = context.getUri();

        String serviceId = context.getServiceId();

        if (serviceId != null && !serviceId.equals("")) {
            SpringClientFactory clientFactory = getClientFactory();
            IClientConfig clientConfig = clientFactory.getClientConfig(serviceId);
            // use the configurations in archaius to config the IClientConfig
            clientConfig.loadProperties(serviceId);
            RestClient restClient = clientFactory.getClient(serviceId, RestClient.class);

            RestClientRibbonCommand command = null;
            try {
                command = new RestClientRibbonCommand(
                    serviceId, restClient, getVerb(context.getVerb()),
                    context.getUri(), context.getRetryable(), context.getHeaders(),
                    context.getParams(), context.getRequestEntity());
            } catch (URISyntaxException e) {
                log.error("create client error for service: " +serviceId,e);
            }
            return command;
        }

        // TODO: Add other rules here.
        return super.create(context);
    }
}
