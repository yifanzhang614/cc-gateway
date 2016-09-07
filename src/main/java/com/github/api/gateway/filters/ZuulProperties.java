package com.github.api.gateway.filters;

import com.github.api.gateway.util.StringUtils;
import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ClassUtils;

import java.util.*;

/**
 * Created by yifanzhang.
 */
@AllArgsConstructor
@NoArgsConstructor
public class ZuulProperties {

    /**
     * Headers that are generally expected to be added by Spring Security, and hence often
     * duplicated if the proxy and the backend are secured with Spring. By default they
     * are added to the ignored headers if Spring Security is present.
     */
    private static final List<String> SECURITY_HEADERS = Arrays
        .asList("Pragma", "Cache-Control", "X-Frame-Options", "X-Content-Type-Options",
            "X-XSS-Protection", "Expires");

    /**
     * A common prefix for all routes.
     */
    private String prefix = "";

    /**
     * Flag saying whether to strip the prefix from the path before forwarding.
     */
    private boolean stripPrefix = true;

    /**
     * Flag for whether retry is supported by default (assuming the routes themselves
     * support it).
     */
    private Boolean retryable;

    /**
     * Map of route names to properties.
     */
    private Map<String, ZuulRoute> routes = new LinkedHashMap<String,ZuulRoute>();

    /**
     * Flag to determine whether the proxy adds X-Forwarded-* headers.
     */
    private boolean addProxyHeaders = true;

    /**
     * Set of service names not to consider for proxying automatically. By default all
     * services in the discovery client will be proxied.
     */
    private Set<String> ignoredServices = new LinkedHashSet<String>();

    private Set<String> ignoredPatterns = new LinkedHashSet<String>();

    /**
     * Names of HTTP headers to ignore completely (i.e. leave them out of downstream
     * requests and drop them from downstream responses).
     */
    private Set<String> ignoredHeaders = new LinkedHashSet<String>();

    /**
     * Path to install Zuul as a servlet (not part of Spring MVC). The servlet is more
     * memory efficient for requests with large bodies, e.g. file uploads.
     */
    private String servletPath = "/zuul";

    private boolean ignoreLocalService = true;

    /**
     * Host properties controlling default connection pool properties.
     */
    private Host host = new Host();


    /**
     * Flag to say that path elelents past the first semicolon can be dropped.
     */
    private boolean removeSemicolonContent = true;

    /**
     * List of sensitive headers that are not passed to downstream requests. Defaults
     * to a "safe" set of headers that commonly contain user credentials. It's OK to
     * remove those from the list if the downstream service is part of the same system
     * as the proxy, so they are sharing authentication data. If using a physical URL
     * outside your own domain, then generally it would be a bad idea to leak user
     * credentials.
     */
    private Set<String> sensitiveHeaders = new LinkedHashSet<String>(
        Arrays.asList("Cookie", "Set-Cookie", "Authorization"));

    public Set<String> getIgnoredHeaders() {
        Set<String> ignoredHeaders = new LinkedHashSet<String>(this.ignoredHeaders);
        if (ClassUtils
            .isPresent("org.springframework.security.config.annotation.web.WebSecurityConfigurer",
                null) && Collections.disjoint(ignoredHeaders, SECURITY_HEADERS)) {
            // Allow Spring Security in the gateway to control these headers
            ignoredHeaders.addAll(SECURITY_HEADERS);
        }
        return ignoredHeaders;
    }

    public void setIgnoredHeaders(Set<String> ignoredHeaders) {
        this.ignoredHeaders.addAll(ignoredHeaders);
    }

    public void init() {
        for (Map.Entry<String, ZuulRoute> entry : this.routes.entrySet()) {
            ZuulRoute value = entry.getValue();
            if (!StringUtils.hasText(value.getLocation())) {
                value.serviceId = entry.getKey();
            }
            if (!StringUtils.hasText(value.getId())) {
                value.id = entry.getKey();
            }
            if (!StringUtils.hasText(value.getPath())) {
                value.path = "/" + entry.getKey() + "/**";
            }
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class ZuulRoute {

        /**
         * The ID of the route (the same as its map key by default).
         */
        private String id;

        /**
         * The path (pattern) for the route, e.g. /foo/**.
         */
        private String path;

        /**
         * The service ID (if any) to map to this route. You can specify a physical URL or
         * a service, but not both.
         */
        private String serviceId;

        /**
         * A full physical URL to map to the route. An alternative is to use a service ID
         * and service discovery to find the physical address.
         */
        private String url;

        /**
         * Flag to determine whether the prefix for this route (the path, minus pattern
         * patcher) should be stripped before forwarding.
         */
        private boolean stripPrefix = true;

        /**
         * Flag to indicate that this route should be retryable (if supported). Generally
         * retry requires a service ID and ribbon.
         */
        private Boolean retryable;

        /**
         * List of sensitive headers that are not passed to downstream requests. Defaults
         * to a "safe" set of headers that commonly contain user credentials. It's OK to
         * remove those from the list if the downstream service is part of the same system
         * as the proxy, so they are sharing authentication data. If using a physical URL
         * outside your own domain, then generally it would be a bad idea to leak user
         * credentials.
         */
        private Set<String> sensitiveHeaders = new LinkedHashSet<String>();

        public ZuulRoute() {
        }

        public ZuulRoute(String text) {
            String location = null;
            String path = text;
            if (text.contains("=")) {
                String[] values = StringUtils
                    .trimArrayElements(StringUtils.split(text, "="));
                location = values[1];
                path = values[0];
            }
            this.id = extractId(path);
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            setLocation(location);
            this.path = path;
        }

        public ZuulRoute(String path, String location) {
            this.id = extractId(path);
            this.path = path;
            setLocation(location);
        }

        public String getLocation() {
            if (StringUtils.hasText(this.url)) {
                return this.url;
            }
            return this.serviceId;
        }

        public void setLocation(String location) {
            if (location != null
                && (location.startsWith("http:") || location.startsWith("https:"))) {
                this.url = location;
            }
            else {
                this.serviceId = location;
            }
        }

        private String extractId(String path) {
            path = path.startsWith("/") ? path.substring(1) : path;
            path = path.replace("/*", "").replace("*", "");
            return path;
        }

        public Route getRoute(String prefix) {
            return new Route(this.id, this.path, getLocation(), prefix, this.retryable,
                this.sensitiveHeaders);
        }

        public String getId() {
            return id;
        }

        public String getPath() {
            return path;
        }

        public String getServiceId() {
            return serviceId;
        }

        public String getUrl() {
            return url;
        }

        public boolean isStripPrefix() {
            return stripPrefix;
        }

        public Boolean getRetryable() {
            return retryable;
        }

        public Set<String> getSensitiveHeaders() {
            return sensitiveHeaders;
        }

        public void setSensitiveHeaders(Set<String> sensitiveHeaders) {
            this.sensitiveHeaders = sensitiveHeaders;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setStripPrefix(boolean stripPrefix) {
            this.stripPrefix = stripPrefix;
        }

        public void setRetryable(Boolean retryable) {
            this.retryable = retryable;
        }

        @Override public int hashCode() {
            return Objects
                .hashCode(id, path, serviceId, url, stripPrefix, retryable, sensitiveHeaders);
        }

        @Override public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final ZuulRoute other = (ZuulRoute) obj;
            return Objects.equal(this.id, other.id) && Objects.equal(this.path, other.path)
                && Objects.equal(this.serviceId, other.serviceId) && Objects
                .equal(this.url, other.url) && Objects.equal(this.stripPrefix, other.stripPrefix)
                && Objects.equal(this.retryable, other.retryable) && Objects
                .equal(this.sensitiveHeaders, other.sensitiveHeaders);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Host{
        /**
         * The maximum number of total connections the proxy can hold open to backends.
         */
        private int maxTotalConnections = 200;
        /**
         * The maximum number of connections that can be used by a single route.
         */
        private int maxPerRouteConnections = 20;
    }

    public String getServletPattern() {
        String path = this.servletPath;
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.contains("*")) {
            path = path.endsWith("/") ? (path + "*") : (path + "/*");
        }
        return path;
    }

    public String getServletPath() {
        return servletPath;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isStripPrefix() {
        return stripPrefix;
    }

    public Boolean getRetryable() {
        return retryable;
    }

    public Map<String, ZuulRoute> getRoutes() {
        return routes;
    }

    public Set<String> getSensitiveHeaders() {
        return sensitiveHeaders;
    }

    public Set<String> getIgnoredServices() {
        return ignoredServices;
    }

    public Set<String> getIgnoredPatterns() {
        return ignoredPatterns;
    }

    public boolean isIgnoreLocalService() {
        return ignoreLocalService;
    }

    public void setSensitiveHeaders(Set<String> sensitiveHeaders) {
        this.sensitiveHeaders = sensitiveHeaders;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setStripPrefix(boolean stripPrefix) {
        this.stripPrefix = stripPrefix;
    }

    public void setRetryable(Boolean retryable) {
        this.retryable = retryable;
    }

    public void setRoutes(Map<String, ZuulRoute> routes) {
        this.routes = routes;
    }

    public void setAddProxyHeaders(boolean addProxyHeaders) {
        this.addProxyHeaders = addProxyHeaders;
    }

    public void setIgnoredServices(Set<String> ignoredServices) {
        this.ignoredServices = ignoredServices;
    }

    public void setIgnoredPatterns(Set<String> ignoredPatterns) {
        this.ignoredPatterns = ignoredPatterns;
    }

    public void setServletPath(String servletPath) {
        this.servletPath = servletPath;
    }

    public void setIgnoreLocalService(boolean ignoreLocalService) {
        this.ignoreLocalService = ignoreLocalService;
    }

    public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
        this.removeSemicolonContent = removeSemicolonContent;
    }

    public static List<String> getSecurityHeaders() {
        return SECURITY_HEADERS;
    }

    public boolean isAddProxyHeaders() {
        return addProxyHeaders;
    }

    public boolean isRemoveSemicolonContent() {
        return removeSemicolonContent;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ZuulProperties that = (ZuulProperties) o;
        return Objects.equal(stripPrefix, that.stripPrefix) &&
            Objects.equal(addProxyHeaders, that.addProxyHeaders) &&
            Objects.equal(ignoreLocalService, that.ignoreLocalService) &&
            Objects.equal(removeSemicolonContent, that.removeSemicolonContent) &&
            Objects.equal(prefix, that.prefix) &&
            Objects.equal(retryable, that.retryable) &&
            Objects.equal(routes, that.routes) &&
            Objects.equal(ignoredServices, that.ignoredServices) &&
            Objects.equal(ignoredPatterns, that.ignoredPatterns) &&
            Objects.equal(ignoredHeaders, that.ignoredHeaders) &&
            Objects.equal(servletPath, that.servletPath) &&
            Objects.equal(sensitiveHeaders, that.sensitiveHeaders);
    }

    @Override public int hashCode() {
        return Objects
            .hashCode(prefix, stripPrefix, retryable, routes, addProxyHeaders, ignoredServices,
                ignoredPatterns, ignoredHeaders, servletPath, ignoreLocalService,
                removeSemicolonContent, sensitiveHeaders);
    }
}
