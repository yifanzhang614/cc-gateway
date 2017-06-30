package com.github.api.gateway.filters.route.ribbon;

import com.netflix.hystrix.HystrixExecutable;
import org.springframework.http.client.ClientHttpResponse;

/**
 * Created by yifanzhang.
 */
public interface RibbonCommand extends HystrixExecutable<ClientHttpResponse>{
    public static final String DEFAULT_CONFIG_REFIX = "zuul.cc-gateway.";
}
