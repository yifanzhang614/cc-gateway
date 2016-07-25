package com.github.api.gateway.filters;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by yifanzhang.
 */
public class ZuulPropertiesTests {
    private ZuulProperties zuul;

    @Before
    public void setup() {
        this.zuul = new ZuulProperties();
    }

    @After
    public void teardown() {
        this.zuul = null;
    }

    @Test
    public void defaultIgnoredHeaders() {
        assertTrue(this.zuul.getIgnoredHeaders().isEmpty());
    }

    @Test
    public void addIgnoredHeaders() {
        this.zuul.setIgnoredHeaders(Collections.singleton("x-foo"));
        assertTrue(this.zuul.getIgnoredHeaders().contains("x-foo"));
    }

    @Test
    public void defaultSensitiveHeaders() {
        ZuulProperties.ZuulRoute route = new ZuulProperties.ZuulRoute("foo");
        this.zuul.getRoutes().put("foo", route);
        assertTrue(this.zuul.getRoutes().get("foo").getSensitiveHeaders().isEmpty());
        assertTrue(this.zuul.getSensitiveHeaders().containsAll(
            Arrays.asList("Cookie", "Set-Cookie", "Authorization")));
    }

    @Test
    public void addSensitiveHeaders() {
        this.zuul.setSensitiveHeaders(Collections.singleton("x-bar"));
        ZuulProperties.ZuulRoute route = new ZuulProperties.ZuulRoute("foo");
        route.setSensitiveHeaders(Collections.singleton("x-foo"));
        this.zuul.getRoutes().put("foo", route);
        ZuulProperties.ZuulRoute foo = this.zuul.getRoutes().get("foo");
        assertTrue(foo.getSensitiveHeaders().contains("x-foo"));
        assertFalse(foo.getSensitiveHeaders().contains("Cookie"));
        assertTrue(this.zuul.getSensitiveHeaders().contains("x-bar"));
        assertFalse(this.zuul.getSensitiveHeaders().contains("Cookie"));
    }
}
