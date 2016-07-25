package com.github.api.gateway.filters.route;

import com.netflix.zuul.context.RequestContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by yifanzhang.
 */
public class SendForwardFilterTests {

    @After
    public void reset() {
        RequestContext.testSetCurrentContext(null);
    }

    @Before
    public void setTestRequestcontext() {
        RequestContext context = new RequestContext();
        RequestContext.testSetCurrentContext(context);
    }

    @Test
    public void runsNormally() {
        SendForwardFilter filter = createSendForwardFilter(new MockHttpServletRequest());
        assertTrue("shouldFilter returned false", filter.shouldFilter());
        filter.run();
    }

    private SendForwardFilter createSendForwardFilter(HttpServletRequest request) {
        RequestContext context = new RequestContext();
        context.setRequest(request);
        context.setResponse(new MockHttpServletResponse());
        context.set("forward.to", "/foo");
        RequestContext.testSetCurrentContext(context);
        SendForwardFilter filter = new SendForwardFilter();
        return filter;
    }

    @Test
    public void doesNotRunTwice() {
        SendForwardFilter filter = createSendForwardFilter(new MockHttpServletRequest());
        assertTrue("shouldFilter returned false", filter.shouldFilter());
        filter.run();
        assertFalse("shouldFilter returned true", filter.shouldFilter());
    }
}
