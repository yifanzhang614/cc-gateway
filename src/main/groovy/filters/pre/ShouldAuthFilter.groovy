import com.github.api.gateway.filters.ZuulFilterType
import com.github.api.gateway.support.auth.AuthenticatingResult
import com.github.api.gateway.support.auth.ExcuteState
import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext

/**
 * Created by chdyan on 16/7/31.
 */


public  class ShouldAuthFilter extends ZuulFilter {

    static final DEFAULT_NAME = "isAuth";

    String paramName = DEFAULT_NAME;

    @Override
    String filterType() {
        return ZuulFilterType.PRE.getType();
    }

    @Override
    int filterOrder() {
        return 10
    }

    @Override
    boolean shouldFilter() {
        return true
    }

    @Override
    Object run() {
        RequestContext ctx = RequestContext.getCurrentContext()
        Object r = ctx.getRequest().getParameter(paramName);
        AuthenticatingResult result = new AuthenticatingResult()
        if('false'.equals(r)) {
            result.setAuth(ExcuteState.EXCUTE);
        }
        ctx.set(AuthenticatingResult.AUTHENTICATING_RESULT, result)
        return null
    }

}