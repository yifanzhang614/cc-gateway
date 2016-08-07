import com.github.api.gateway.authc.authenticator.ModularProviderAuthenticator
import com.github.api.gateway.authc.exception.AuthenticationException
import com.github.api.gateway.filters.ZuulFilterType
import com.github.api.gateway.provider.properties.PropertyCcSignatureProvider
import com.github.api.gateway.provider.properties.PropertyUsernamePasswordProvider
import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext

/**
 * Created by chdyan on 16/8/3.
 */

/**
 * Is authentication Success
 */
class AuthenticateFilter extends ZuulFilter {

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
        def authenticator = newModularProviderAuthenticator();
        authenticator.authenticate(ctx.getRequest());
    }

    ModularProviderAuthenticator newModularProviderAuthenticator() {
        ModularProviderAuthenticator authenticator = new ModularProviderAuthenticator();
        authenticator.getProviders().add(new PropertyUsernamePasswordProvider());
        authenticator.getProviders().add(new PropertyCcSignatureProvider());
        return authenticator;
    }


}