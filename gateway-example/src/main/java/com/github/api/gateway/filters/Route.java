package com.github.api.gateway.filters;

import com.github.gateway.util.StringUtils;
import com.google.common.base.Objects;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by yifanzhang.
 */

public class Route {

    public Route(String id, String path, String location, String prefix,
        Boolean retryable, Set<String> ignoredHeaders) {
        this.id = id;
        this.prefix = StringUtils.hasText(prefix) ? prefix : "";
        this.path = path;
        this.fullPath = prefix + path;
        this.location = location;
        this.retryable = retryable;
        this.sensitiveHeaders = new LinkedHashSet<String>();
        if (ignoredHeaders != null) {
            for (String header : ignoredHeaders) {
                this.sensitiveHeaders.add(header.toLowerCase());
            }
        }
    }

    private String id;

    private String fullPath;

    private String path;

    private String location;

    private String prefix;

    private Boolean retryable;

    private Set<String> sensitiveHeaders = new LinkedHashSet<String>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Boolean getRetryable() {
        return retryable;
    }

    public void setRetryable(Boolean retryable) {
        this.retryable = retryable;
    }

    public Set<String> getSensitiveHeaders() {
        return sensitiveHeaders;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Route route = (Route) o;
        return Objects.equal(id, route.id) &&
            Objects.equal(fullPath, route.fullPath) &&
            Objects.equal(path, route.path) &&
            Objects.equal(location, route.location) &&
            Objects.equal(prefix, route.prefix) &&
            Objects.equal(retryable, route.retryable) &&
            Objects.equal(sensitiveHeaders, route.sensitiveHeaders);
    }

    @Override public int hashCode() {
        return Objects.hashCode(id, fullPath, path, location, prefix, retryable, sensitiveHeaders);
    }
}
