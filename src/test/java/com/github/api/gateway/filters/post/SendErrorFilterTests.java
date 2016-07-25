package com.github.api.gateway.filters.post;

import com.netflix.zuul.context.RequestContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;


/**
 * Created by yifanzhang.
 */
public class SendErrorFilterTests {

    @Before
    public void setTestRequestcontext() {
        RequestContext context = new RequestContext();
        RequestContext.testSetCurrentContext(context);
    }

    @After
    public void reset() {
        RequestContext.getCurrentContext().clear();
    }

    @Test
    public void runsNormally() {
        SendErrorFilter filter = createSendErrorFilter(new MockHttpServletRequest());
        assertTrue("shouldFilter returned false", filter.shouldFilter());
        filter.run();
    }

    private SendErrorFilter createSendErrorFilter(HttpServletRequest request) {
        RequestContext context = new RequestContext();
        context.setRequest(request);
        context.setResponse(new MockHttpServletResponse());
        context.set("error.status_code", HttpStatus.NOT_FOUND.value());
        RequestContext.testSetCurrentContext(context);
        SendErrorFilter filter = new SendErrorFilter();
        filter.setErrorPath("/error");
        return filter;
    }

    @Test
    public void noRequestDispatcher() {
        SendErrorFilter filter = createSendErrorFilter(mock(HttpServletRequest.class));
        assertTrue("shouldFilter returned false", filter.shouldFilter());
        filter.run();
    }

    @Test
    public void doesNotRunTwice() {
        SendErrorFilter filter = createSendErrorFilter(new MockHttpServletRequest());
        assertTrue("shouldFilter returned false", filter.shouldFilter());
        filter.run();
        assertFalse("shouldFilter returned true", filter.shouldFilter());
    }
}
