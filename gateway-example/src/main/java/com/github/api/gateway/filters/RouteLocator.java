package com.github.api.gateway.filters;

import java.util.Collection;
import java.util.List;

/**
 * Created by yifanzhang.
 */
public interface RouteLocator {

    /**
     * Ignored route paths (or patterns), if any.
     */
    Collection<String> getIgnoredPaths();

    /**
     * A map of route path (pattern) to location (e.g. service id or URL).
     */
    List<Route> getRoutes();

    /**
     * Maps a path to an actual route with full metadata.
     */
    Route getMatchingRoute(String path);

}
