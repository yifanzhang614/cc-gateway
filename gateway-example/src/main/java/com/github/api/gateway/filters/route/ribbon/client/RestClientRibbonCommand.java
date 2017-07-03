package com.github.api.gateway.filters.route.ribbon.client;

import com.github.api.gateway.filters.route.ribbon.RibbonCommand;
import com.github.api.gateway.filters.route.ribbon.RibbonHttpResponse;
import com.netflix.client.http.HttpRequest;
import com.netflix.client.http.HttpRequest.Builder;
import com.netflix.client.http.HttpRequest.Verb;
import com.netflix.client.http.HttpResponse;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.niws.client.http.RestClient;
import com.netflix.zuul.context.RequestContext;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by yifanzhang.
 */
public class RestClientRibbonCommand extends HystrixCommand<ClientHttpResponse> implements
    RibbonCommand {

    private RestClient restClient;

    private Verb verb;

    private URI uri;

    private Boolean retryable;

    private MultiValueMap<String, String> headers;

    private MultiValueMap<String, String> params;

    private InputStream requestEntity;

    public RestClientRibbonCommand(RestClient restClient, Verb verb, String uri,
        Boolean retryable,
        MultiValueMap<String, String> headers,
        MultiValueMap<String, String> params, InputStream requestEntity)
        throws URISyntaxException {
        this("default", restClient, verb, uri, retryable , headers, params, requestEntity);
    }

    public RestClientRibbonCommand(String commandKey, RestClient restClient, Verb verb, String uri,
        Boolean retryable,
        MultiValueMap<String, String> headers,
        MultiValueMap<String, String> params, InputStream requestEntity)
        throws URISyntaxException {
        super(getSetter(commandKey));
        this.restClient = restClient;
        this.verb = verb;
        this.uri = new URI(uri);
        this.retryable = retryable;
        this.headers = headers;
        this.params = params;
        this.requestEntity = requestEntity;
    }

    protected static HystrixCommand.Setter getSetter(String commandKey) {
        // we want to default to semaphore-isolation since this wraps
        // 2 others commands that are already thread isolated
        String name = DEFAULT_CONFIG_REFIX + commandKey + ".semaphore.maxSemaphores";
        DynamicIntProperty value = DynamicPropertyFactory.getInstance().getIntProperty(
            name, 100);
        HystrixCommandProperties.Setter setter = HystrixCommandProperties.Setter()
            .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
            .withExecutionIsolationSemaphoreMaxConcurrentRequests(value.get());
        return Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("RibbonCommand"))
            .andCommandKey(HystrixCommandKey.Factory.asKey(commandKey))
            .andCommandPropertiesDefaults(setter);
    }

    @Override
    protected ClientHttpResponse run() throws Exception {
        return forward();
    }

    protected ClientHttpResponse forward() throws Exception {
        RequestContext context = RequestContext.getCurrentContext();
        Builder builder = HttpRequest.newBuilder().verb(this.verb).uri(this.uri)
            .entity(this.requestEntity);
        if (this.retryable != null) {
            builder.setRetriable(this.retryable);
        }

        for(String name :this.params.keySet()) {
            List<String> values = this.params.get(name);
            for (String value : values) {
                builder.queryParam(name,value);
            }
        }

        customizeRequest(builder);

        HttpRequest httpClientRequest = builder.build();
        HttpResponse response = this.restClient
            .executeWithLoadBalancer(httpClientRequest);
        context.set("ribbonResponse", response);

        // Explicitly close the HttpResponse if the Hystrix command timed out to
        // release the underlying HTTP connection held by the response.
        //
        if( this.isResponseTimedOut() ) {
            if( response!= null ) {
                response.close();
            }
        }

        RibbonHttpResponse ribbonHttpResponse = new RibbonHttpResponse(response);

        return ribbonHttpResponse;
    }

    protected void customizeRequest(Builder requestBuilder) {
    }

    protected MultiValueMap<String, String> getHeaders() {
        return this.headers;
    }

    protected MultiValueMap<String, String> getParams() {
        return this.params;
    }

    protected InputStream getRequestEntity() {
        return this.requestEntity;
    }

    protected RestClient getRestClient() {
        return this.restClient;
    }

    protected Boolean getRetryable() {
        return this.retryable;
    }

    protected URI getUri() {
        return this.uri;
    }

    protected Verb getVerb() {
        return this.verb;
    }
}
