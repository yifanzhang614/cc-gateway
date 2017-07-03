package com.github.api.gateway.filters;

import com.github.api.gateway.util.AntPathMatcher;
import com.github.api.gateway.util.PathMatcher;
import com.github.api.gateway.util.RequestUtils;
import com.github.gateway.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by yifanzhang.
 */
public class SimpleRouteLocator implements RouteLocator {
    private static final Logger log = LoggerFactory.getLogger(SimpleRouteLocator.class);

    private ZuulProperties properties;

    private PathMatcher pathMatcher = new AntPathMatcher();

    private String dispatcherServletPath = "/";
    private String zuulServletPath;

    private AtomicReference<Map<String, ZuulProperties.ZuulRoute>> routes =
        new AtomicReference<Map<String, ZuulProperties.ZuulRoute>>();

    public SimpleRouteLocator(String servletPath, ZuulProperties properties) {
        this.properties = properties;
        if (servletPath != null && StringUtils.hasText(servletPath)) {
            this.dispatcherServletPath = servletPath;
        }

        this.zuulServletPath = properties.getServletPath();
    }

    public List<Route> getRoutes() {
        if (this.routes.get() == null) {
            this.routes.set(locateRoutes());
        }
        List<Route> values = new ArrayList<Route>();
        for (String url : this.routes.get().keySet()) {
            ZuulProperties.ZuulRoute route = this.routes.get().get(url);
            String path = route.getPath();
            values.add(getRoute(route, path));
        }
        return values;
    }

    @Override
    public Route getMatchingRoute(final String path) {
        if (log.isDebugEnabled()) {
            log.debug("Finding route for path: " + path);
        }

        if (this.routes.get() == null) {
            this.routes.set(locateRoutes());
        }

//        log.debug("servletPath=" + this.dispatcherServletPath);
//        log.debug("zuulServletPath=" + this.zuulServletPath);
//        log.debug("RequestUtils.isDispatcherServletRequest()=" + RequestUtils.isDispatcherServletRequest());
//        log.debug("RequestUtils.isZuulServletRequest()=" + RequestUtils.isZuulServletRequest());

        String adjustedPath = adjustPath(path);

        ZuulProperties.ZuulRoute route = null;
        if (!matchesIgnoredPatterns(adjustedPath)) {
            for (Map.Entry<String, ZuulProperties.ZuulRoute> entry : this.routes.get().entrySet()) {
                String pattern = entry.getKey();
                log.debug("Matching pattern:" + pattern);
                if (this.pathMatcher.match(pattern, adjustedPath)) {
                    route = entry.getValue();
                    break;
                }
            }
        }
        log.debug("route matched=" + route);
        return getRoute(route,adjustedPath);
    }

    @Override
    public Collection<String> getIgnoredPaths() {
        return this.properties.getIgnoredPatterns();
    }

    public void refresh() {
        doRefresh();
    }
    public void addRoute(String path, String location) {
        this.properties.getRoutes().put(path, new ZuulProperties.ZuulRoute(path, location));
        refresh();
    }

    public void addRoute(ZuulProperties.ZuulRoute route) {
        this.properties.getRoutes().put(route.getPath(), route);
        refresh();
    }

    protected void addConfiguredRoutes(Map<String,ZuulProperties.ZuulRoute> routes) {
        Map<String,ZuulProperties.ZuulRoute> routeEntries = this.properties.getRoutes();
        for (ZuulProperties.ZuulRoute entry : routeEntries.values()) {
            String route = entry.getPath();
            if (routes.containsKey(route)) {
                log.warn("Overwriting route " + route + ": already defined by "
                    + routes.get(route));
            }
            routes.put(route,entry);
        }
    }
    /**
     * Calculate all the routes and set up a cache for the values. Subclasses can call
     * this method if they need to implement {@link RefreshableRouteLocator}.
     */
    protected void doRefresh() {
        this.routes.set(locateRoutes());
    }

    protected Map<String, ZuulProperties.ZuulRoute> locateRoutes() {

        LinkedHashMap<String,ZuulProperties.ZuulRoute> routesMap = new LinkedHashMap<String, ZuulProperties.ZuulRoute>();
        for (ZuulProperties.ZuulRoute route : this.properties.getRoutes().values()) {
            routesMap.put(route.getPath(),route);
        }
        return routesMap;
    }

    private Route getRoute(ZuulProperties.ZuulRoute route, String path) {
        if (route == null) {
            return null;
        }
        String targetPath = path;
        String prefix = this.properties.getPrefix();
        if (path.startsWith(prefix) && this.properties.isStripPrefix()) {
            targetPath = path.substring(prefix.length());
        }
        if (route.isStripPrefix()) {
            int index = route.getPath().indexOf("*") - 1;
            if (index > 0) {
                String routePrefix = route.getPath().substring(0,index);
                targetPath = targetPath.replaceFirst(routePrefix,"");
                prefix = prefix + routePrefix;
            }
        }
        Boolean retryable = this.properties.getRetryable();
        if (route.getRetryable() != null) {
            retryable = route.getRetryable();
        }
        return new Route(route.getId(),targetPath,route.getLocation(),prefix,
            retryable,route.getSensitiveHeaders());
    }

    protected boolean matchesIgnoredPatterns(String path) {
        for (String pattern : this.properties.getIgnoredPatterns()) {
            log.debug("Matching ignored pattern:" + pattern);
            if (this.pathMatcher.match(pattern, path)) {
                log.debug("Path " + path + " matches ignored pattern " + pattern);
                return true;
            }
        }
        return false;
    }

    private String adjustPath(final String path) {
        String adjustedPath = path;

        if (path != null && path.equals("/")) {
            return path;
        }

        if (RequestUtils.isDispatcherServletRequest()
            && StringUtils.hasText(this.dispatcherServletPath)) {
            if (!this.dispatcherServletPath.equals("/")) {
                adjustedPath = path.substring(this.dispatcherServletPath.length());
                log.debug("Stripped dispatcherServletPath");
            }
        }
        else if (RequestUtils.isZuulServletRequest()) {
            if (StringUtils.hasText(this.zuulServletPath)
                && !this.zuulServletPath.equals("/")) {
                adjustedPath = path.substring(this.zuulServletPath.length());
                log.debug("Stripped zuulServletPath");
            }
        }
        else {
            // do nothing
        }

        log.debug("adjustedPath=" + path);
        return adjustedPath;
    }
}
