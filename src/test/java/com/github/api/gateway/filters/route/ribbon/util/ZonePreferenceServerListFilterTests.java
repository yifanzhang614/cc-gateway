package com.github.api.gateway.filters.route.ribbon.util;

import com.netflix.loadbalancer.Server;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by yifanzhang.
 */
public class ZonePreferenceServerListFilterTests {
    private Server dsyer = new Server("dsyer", 8080);
    private Server localhost = new Server("localhost", 8080);

    @Before
    public void init() {
        this.dsyer.setZone("dsyer");
        this.localhost.setZone("localhost");
    }

    @Test
    public void noZoneSet() {
        ZonePreferenceServerListFilter filter = new ZonePreferenceServerListFilter();
        List<Server> result = filter.getFilteredListOfServers(Arrays
            .asList(this.localhost));
        assertEquals(1, result.size());
    }

    @Test
    public void withZoneSetAndNoMatches() {
        ZonePreferenceServerListFilter filter = new ZonePreferenceServerListFilter();
        ReflectionTestUtils.setField(filter, "zone", "dsyer");
        List<Server> result = filter.getFilteredListOfServers(Arrays
            .asList(this.localhost));
        assertEquals(1, result.size());
    }

    @Test
    public void withZoneSetAndMatches() {
        ZonePreferenceServerListFilter filter = new ZonePreferenceServerListFilter();
        ReflectionTestUtils.setField(filter, "zone", "dsyer");
        List<Server> result = filter.getFilteredListOfServers(Arrays.asList(this.dsyer,
            this.localhost));
        assertEquals(1, result.size());
    }
}
