package com.github.api.gateway.filters.pre;

import com.github.api.gateway.filters.route.ProxyRequestHelper;
import com.github.api.gateway.filters.Route;
import com.github.api.gateway.filters.RouteLocator;
import com.github.api.gateway.filters.ZuulProperties;
import com.github.api.gateway.util.StringUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.constants.ZuulHeaders;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UrlPathHelper;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by yifanzhang.
 */
public class PreDecorationFilter extends ZuulFilter {

    private static final Logger log = LoggerFactory.getLogger(PreDecorationFilter.class);

    private RouteLocator routeLocator;
    private ZuulProperties properties;
    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    public PreDecorationFilter(RouteLocator routeLocator,
        ZuulProperties properties) {
        this.routeLocator = routeLocator;
        this.properties = properties;
        this.urlPathHelper.setRemoveSemicolonContent(properties.isRemoveSemicolonContent());
    }

    @Override
    public int filterOrder() {
        return 20;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return !ctx.containsKey("forward.to") // a filter has already forwarded
                && !ctx.containsKey("serviceId"); // a filter has already determined serviceId
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        final String requestURI = this.urlPathHelper
            .getPathWithinApplication(ctx.getRequest());
        Route route = this.routeLocator.getMatchingRoute(requestURI);
        if (route != null) {
            String location = route.getLocation();
            if (location != null) {
                ctx.put("requestURI", route.getPath());
                ctx.put("proxy", route.getId());
                if (route.getSensitiveHeaders().isEmpty()) {
                    ctx.put(ProxyRequestHelper.IGNORED_HEADERS, this.properties.getSensitiveHeaders());
                } else {
                    ctx.put(ProxyRequestHelper.IGNORED_HEADERS, route.getSensitiveHeaders());
                }

                if (route.getRetryable() != null) {
                    ctx.put("retryable", route.getRetryable());
                }

                if (location.startsWith("http:") || location.startsWith("https:")) {
                    // for SimpleRoutingFilter to use
                    ctx.setRouteHost(getUrl(location));
                    ctx.addOriginResponseHeader("X-Zuul-Service", location);
                } else if (location.startsWith("forward:")) {
                    ctx.set("forward.to", StringUtils
                        .cleanPath(location.substring("forward:".length()) + route.getPath()));
                    ctx.setRouteHost(null);
                    return null;
                } else {
                    // set serviceId for use for RibbonRoutingFilter
                    ctx.set("serviceId",location);
                    ctx.setRouteHost(null);
                    ctx.addOriginResponseHeader("X-Zuul-Service",location);
                }

                if (this.properties.isAddProxyHeaders()) {
                    ctx.addZuulRequestHeader("X-Forwarded-Host",
                        ctx.getRequest().getServerName());
                    ctx.addZuulRequestHeader("X-Forwarded-Port",
                        String.valueOf(ctx.getRequest().getServerPort()));
                    ctx.addZuulRequestHeader(ZuulHeaders.X_FORWARDED_PROTO,
                        ctx.getRequest().getScheme());
                    if (StringUtils.hasText(route.getPrefix())) {
                        ctx.addZuulRequestHeader("X-Forwarded-Prefix", route.getPrefix());
                    }
                    String xforwardedfor = ctx.getRequest().getHeader("X-Forwarded-For");
                    String remoteAddr = ctx.getRequest().getRemoteAddr();
                    if (xforwardedfor == null) {
                        xforwardedfor = remoteAddr;
                    } else if (!xforwardedfor.contains(remoteAddr)) { // Prevent duplicates
                        xforwardedfor += ", " + remoteAddr;
                    }
                    ctx.addZuulRequestHeader("X-Forwarded-For", xforwardedfor);
                }
            }
        } else {
            // TODO: Can add fall back uri here..
            if (log.isDebugEnabled()) {
                log.debug("No route found for uri: " + requestURI);
            }
            // use the error filter to handle this case.
            ctx.set("error.status_code", "404");
            ctx.set("error.message","Resource not found");
        }

        //        ctx.addOriginResponseHeader("cache-control", "no-cache");
        return null;
    }

    private URL getUrl(String target) {
        try {
            return new URL(target);
        }
        catch (MalformedURLException ex) {
            throw new IllegalStateException("Target URL is malformed", ex);
        }
    }

}
