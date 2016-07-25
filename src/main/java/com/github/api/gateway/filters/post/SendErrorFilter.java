package com.github.api.gateway.filters.post;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ReflectionUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

public class SendErrorFilter extends ZuulFilter {

    private static final Logger log = LoggerFactory.getLogger(SendErrorFilter.class);
    protected static final String SEND_ERROR_FILTER_RAN = "sendErrorFilter.ran";

    @Value("${error.path:/error}")
    private String errorPath;

    @Override public String filterType() {
        return "post";
    }

    @Override public int filterOrder() {
        return 0;
    }

    @Override public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        // only forward to errorPath if it hasn't been forwarded to already
        return ctx.containsKey("error.status_code") && !ctx
            .getBoolean(SEND_ERROR_FILTER_RAN, false);
    }

    @Override public Object run() {
        try {
            RequestContext ctx = RequestContext.getCurrentContext();
            HttpServletRequest request = ctx.getRequest();

            int statusCode = (Integer) ctx.get("error.status_code");
            request.setAttribute("javax.servlet.error.status_code", statusCode);

            if (ctx.containsKey("error.exception")) {
                Object e = ctx.get("error.exception");
                log.warn("Error during filtering", Throwable.class.cast(e));
                request.setAttribute("javax.servlet.error.exception", e);
            }

            if (ctx.containsKey("error.message")) {
                String message = (String) ctx.get("error.message");
                request.setAttribute("javax.servlet.error.message", message);
            }

            RequestDispatcher dispatcher = request.getRequestDispatcher(this.errorPath);
            if (dispatcher != null) {
                ctx.set(SEND_ERROR_FILTER_RAN, true);
                if (!ctx.getResponse().isCommitted()) {
                    dispatcher.forward(request, ctx.getResponse());
                }
            }
        } catch (Exception ex) {
            ReflectionUtils.rethrowRuntimeException(ex);
        }
        return null;
    }

    public void setErrorPath(String errorPath) {
        this.errorPath = errorPath;
    }

}
