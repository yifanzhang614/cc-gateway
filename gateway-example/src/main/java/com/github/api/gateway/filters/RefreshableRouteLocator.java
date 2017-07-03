package com.github.api.gateway.filters;

/**
 * Created by yifanzhang.
 */
public interface RefreshableRouteLocator extends RouteLocator{
    void refresh();
}
