package com.github.api.gateway.filters.route.ribbon.util;

import com.netflix.loadbalancer.Server;

import java.util.Map;

/**
 * Created by yifanzhang.
 */
public interface ServerIntrospector {
    boolean isSecure(Server server);

    Map<String, String> getMetadata(Server server);
}
