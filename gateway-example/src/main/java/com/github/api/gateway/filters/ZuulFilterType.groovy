package com.github.api.gateway.filters
/**
 * Created by chdyan on 16/7/31.
 */

enum ZuulFilterType {
    PRE("pre"), ROUTE("route"), POST("post"), ERROR("error");
    String type;

    ZuulFilterType(String type) {
        this.type = type
    }

    String getType() {
        return type
    }
}
