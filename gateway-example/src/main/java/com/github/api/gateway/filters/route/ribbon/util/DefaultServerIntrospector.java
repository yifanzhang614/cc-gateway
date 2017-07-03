package com.github.api.gateway.filters.route.ribbon.util;

import com.netflix.loadbalancer.Server;

import java.util.Collections;
import java.util.Map;

/**
 * Created by yifanzhang.
 */
public class DefaultServerIntrospector implements ServerIntrospector {
    @Override public boolean isSecure(Server server) {
        return (""+server.getPort()).endsWith("443");
    }

    @Override public Map<String, String> getMetadata(Server server) {
        return Collections.emptyMap();
    }
}
