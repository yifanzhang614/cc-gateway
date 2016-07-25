package com.github.api.gateway.filters.route.ribbon.client.apache;

import com.github.api.gateway.filters.route.ribbon.RibbonCommand;
import com.github.api.gateway.filters.route.ribbon.RibbonHttpResponse;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.zuul.context.RequestContext;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.MultiValueMap;

import java.io.InputStream;
import java.net.URI;

/**
 * Created by yifanzhang.
 */
public class HttpClientRibbonCommand extends HystrixCommand<ClientHttpResponse> implements
    RibbonCommand {

    private final RibbonLoadBalancingHttpClient client;
    private final String method;
    private final String uri;
    private final MultiValueMap<String, String> headers;
    private final MultiValueMap<String, String> params;
    private final InputStream requestEntity;
    private final Boolean retryable;

    public HttpClientRibbonCommand(final RibbonLoadBalancingHttpClient client,
        final String method, final String uri,
        final MultiValueMap<String, String> headers,
        final MultiValueMap<String, String> params, final InputStream requestEntity,
        final Boolean retryable) {
        this("default", client, method, uri, headers, params, requestEntity, retryable);
    }

    public HttpClientRibbonCommand(final String commandKey,
        final RibbonLoadBalancingHttpClient client, final String method,
        final String uri, final MultiValueMap<String, String> headers,
        final MultiValueMap<String, String> params, final InputStream requestEntity,
        final Boolean retryable) {
        super(getSetter(commandKey));
        this.client = client;
        this.method = method;
        this.uri = uri;
        this.headers = headers;
        this.params = params;
        this.requestEntity = requestEntity;
        this.retryable = retryable;
    }

    protected static Setter getSetter(final String commandKey) {

        // we want to default to semaphore-isolation since this wraps
        // 2 others commands that are already thread isolated
        final String name = DEFAULT_CONFIG_REFIX + commandKey
            + ".semaphore.maxSemaphores";
        final DynamicIntProperty value = DynamicPropertyFactory.getInstance()
            .getIntProperty(name, 100);
        final HystrixCommandProperties.Setter setter = HystrixCommandProperties
            .Setter()
            .withExecutionIsolationStrategy(
                HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
            .withExecutionIsolationSemaphoreMaxConcurrentRequests(value.get());
        return Setter
            .withGroupKey(HystrixCommandGroupKey.Factory.asKey("RibbonCommand"))
            .andCommandKey(
                HystrixCommandKey.Factory.asKey(commandKey + "RibbonCommand"))
            .andCommandPropertiesDefaults(setter);
    }

    @Override
    protected ClientHttpResponse run() throws Exception {
        return forward();
    }

    protected ClientHttpResponse forward() throws Exception {
        final RequestContext context = RequestContext.getCurrentContext();
        URI uriInstance = new URI(this.uri);
        RibbonApacheHttpRequest request = new RibbonApacheHttpRequest(this.method,
            uriInstance, this.retryable, this.headers, this.params,
            this.requestEntity);
        final RibbonApacheHttpResponse response = this.client
            .executeWithLoadBalancer(request);
        context.set("ribbonResponse", response);
        // Explicitly close the HttpResponse if the Hystrix command timed out to
        // release the underlying HTTP connection held by the response.
        //
        if (this.isResponseTimedOut()) {
            if (response != null) {
                response.close();
            }
        }

        return new RibbonHttpResponse(response);
    }
}
