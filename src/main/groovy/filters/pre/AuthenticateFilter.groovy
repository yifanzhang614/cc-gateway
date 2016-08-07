import com.github.api.gateway.authc.exception.AuthenticationException
import com.github.api.gateway.filters.ZuulFilterType
import com.github.api.gateway.support.auth.AuthenticatingResult
import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext

/**
 * Created by chdyan on 16/8/3.
 */

/**
 * Is authentication Success
 */
class AuthenticateResultFilter extends ZuulFilter {

    @Override
    String filterType() {
        return ZuulFilterType.PRE.getType()
    }

    @Override
    int filterOrder() {
        return 15
    }

    @Override
    boolean shouldFilter() {
        return true
    }

    @Override
    Object run() throws AuthenticationException {
        RequestContext ctx = RequestContext.getCurrentContext();
        AuthenticatingResult result = ctx.get(AuthenticatingResult.AUTHENTICATING_RESULT)
        if(result.authenticatePass()) {
            return true;
        }
        throw new AuthenticationException();
    }
}