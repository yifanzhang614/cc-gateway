import com.github.api.gateway.authc.authenticator.ModularProviderAuthenticator
import com.github.api.gateway.authc.exception.AuthenticationException
import com.github.api.gateway.filters.ZuulFilterType
import com.github.api.gateway.provider.properties.PropertyCcSignatureProvider
import com.github.api.gateway.provider.properties.PropertyUsernamePasswordProvider
import com.netflix.config.DynamicPropertyFactory
import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext

/**
 * 对于是否开启认证过滤，更改shouldFilter的值便可以。
 * 认证Filter，所有的认证过程都是通过ModularProviderAuthenticator来实现的。
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

    /**
     * TODO 改进，通过环境变量控制。
     * 目前通过archaius库获取环境变量来改变
     * 
     * @return
     */
    @Override
    boolean shouldFilter() {
        false
//        return DynamicPropertyFactory.getInstance().getBooleanProperty("authticate", false)
    }

    @Override
    Object run() throws AuthenticationException {
        RequestContext ctx = RequestContext.getCurrentContext()
        def authenticator = newModularProviderAuthenticator()
        authenticator.authenticate(ctx.getRequest())
    }

    /**
     * 创建多模块多验证提供者认证方式，认证的顺序与加入认证提供者的顺序一致。
     * @return
     */
    ModularProviderAuthenticator newModularProviderAuthenticator() {
        ModularProviderAuthenticator authenticator = new ModularProviderAuthenticator()
        authenticator.getProviders().add(new PropertyUsernamePasswordProvider())
        authenticator.getProviders().add(new PropertyCcSignatureProvider())
        authenticator
    }


}