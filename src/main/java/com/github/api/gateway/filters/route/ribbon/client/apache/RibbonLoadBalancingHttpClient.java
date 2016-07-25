package com.github.api.gateway.filters.route.ribbon.client.apache;

import com.netflix.client.AbstractLoadBalancerAwareClient;
import com.netflix.client.RequestSpecificRetryHandler;
import com.netflix.client.RetryHandler;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Created by yifanzhang.
 */
public class RibbonLoadBalancingHttpClient extends
    AbstractLoadBalancerAwareClient<RibbonApacheHttpRequest,RibbonApacheHttpResponse>{
    private final HttpClient delegate = HttpClientBuilder.create().build();

    private int connectTimeout;

    private int readTimeout;

    private boolean secure;

    private boolean followRedirects;

    private boolean okToRetryOnAllOperations;

    public RibbonLoadBalancingHttpClient() {
        super(null);
        this.setRetryHandler(RetryHandler.DEFAULT);
    }

    public RibbonLoadBalancingHttpClient(final ILoadBalancer lb) {
        super(lb);
        this.setRetryHandler(RetryHandler.DEFAULT);
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        super.initWithNiwsConfig(clientConfig);
        this.connectTimeout = clientConfig.getPropertyAsInteger(
            CommonClientConfigKey.ConnectTimeout,
            DefaultClientConfigImpl.DEFAULT_CONNECT_TIMEOUT);
        this.readTimeout = clientConfig.getPropertyAsInteger(
            CommonClientConfigKey.ReadTimeout,
            DefaultClientConfigImpl.DEFAULT_READ_TIMEOUT);
        this.secure = clientConfig.getPropertyAsBoolean(CommonClientConfigKey.IsSecure,
            false);
        this.followRedirects = clientConfig.getPropertyAsBoolean(
            CommonClientConfigKey.FollowRedirects,
            DefaultClientConfigImpl.DEFAULT_FOLLOW_REDIRECTS);
        this.okToRetryOnAllOperations = clientConfig.getPropertyAsBoolean(
            CommonClientConfigKey.OkToRetryOnAllOperations,
            DefaultClientConfigImpl.DEFAULT_OK_TO_RETRY_ON_ALL_OPERATIONS);
    }

    @Override
    public RequestSpecificRetryHandler getRequestSpecificRetryHandler(
        final RibbonApacheHttpRequest request, final IClientConfig requestConfig) {
        if (this.okToRetryOnAllOperations) {
            return new RequestSpecificRetryHandler(true, true, this.getRetryHandler(),
                requestConfig);
        }

        if (!request.getMethod().equals("GET")) {
            return new RequestSpecificRetryHandler(true, false, this.getRetryHandler(),
                requestConfig);
        }
        else {
            return new RequestSpecificRetryHandler(true, true, this.getRetryHandler(),
                requestConfig);
        }
    }

    @Override
    public RibbonApacheHttpResponse execute(RibbonApacheHttpRequest request,
        final IClientConfig configOverride) throws Exception {
        final RequestConfig.Builder builder = RequestConfig.custom();

        if (configOverride != null) {
            builder.setConnectTimeout(configOverride.get(
                CommonClientConfigKey.ConnectTimeout, this.connectTimeout));
            builder.setConnectionRequestTimeout(configOverride.get(
                CommonClientConfigKey.ReadTimeout, this.readTimeout));
            builder.setRedirectsEnabled(configOverride.get(
                CommonClientConfigKey.FollowRedirects, this.followRedirects));
        }
        else {
            builder.setConnectTimeout(this.connectTimeout);
            builder.setConnectionRequestTimeout(this.readTimeout);
            builder.setRedirectsEnabled(this.followRedirects);
        }

        final RequestConfig requestConfig = builder.build();

        if (isSecure(configOverride)) {
            final URI secureUri = UriComponentsBuilder.fromUri(request.getUri())
                .scheme("https").build().toUri();
            request = request.withNewUri(secureUri);
        }

        final HttpUriRequest httpUriRequest = request.toRequest(requestConfig);
        final HttpResponse httpResponse = this.delegate.execute(httpUriRequest);
        return new RibbonApacheHttpResponse(httpResponse, httpUriRequest.getURI());
    }

    private boolean isSecure(final IClientConfig config) {
        return (config != null) ? config.get(CommonClientConfigKey.IsSecure)
            : this.secure;
    }
}
