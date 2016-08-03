import com.github.api.gateway.exception.authentication.AuthenticationRuntimeException
import com.github.api.gateway.exception.authentication.IncorrectCredentialException
import com.github.api.gateway.exception.authentication.IncorrectTimestampRangeException
import com.github.api.gateway.exception.authentication.UnknownAccountException
import com.github.api.gateway.filters.ZuulFilterType
import com.github.api.gateway.support.auth.AuthenticatingResult
import com.github.api.gateway.support.auth.ExcuteState
import com.github.api.gateway.support.property.UserProperties
import com.netflix.ribbon.proxy.annotation.Http
import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import org.apache.commons.lang.StringUtils

import javax.servlet.http.HttpServletRequest

/**
 * Created by chdyan on 16/7/31.
 */
class UsernamePasswordAuthFilter extends ZuulFilter {
    static  final String USERNAME = "api_user";
    static  final String PASSWORD = "api_key";

    static  final String TOKEN = UsernamePasswordAuthFilter.name + ".token";

    String usernameParam = USERNAME;
    String passwordParam = PASSWORD;


    @Override
    String filterType() {
        return ZuulFilterType.PRE.getType();
    }

    @Override
    int filterOrder() {
        return 12;
    }

    @Override
    boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        AuthenticatingResult result = ctx.get(AuthenticatingResult.AUTHENTICATING_RESULT)
        if(!result.authenticatePass()) {
            UsernamePasswordToken token = newUsernamePasswordToken(ctx.getRequest())
            boolean isAuth = token.isShouldParamAuth();
            ctx.set(TOKEN, token);
            if(isAuth) {
                result.setUsernamePasswordAuthState(ExcuteState.EXCUTE);
            }
            return isAuth;
        }
        return false
    }

    //simple implements validate password
    @Override
    Object run() throws AuthenticationRuntimeException {
        RequestContext ctx = RequestContext.getCurrentContext();
        UsernamePasswordToken token = ctx.get(TOKEN);
        String password = UserProperties.get(token.getUsername());
        if(password == null) {
            throw new UnknownAccountException();
        }
        // TODO 简单实现,后续抽象出credentialMatcher类,进行密码,签名等校验
        if(!StringUtils.equals(password, token.getPassword())) {
            throw new IncorrectCredentialException();
        }
    }


    def newUsernamePasswordToken(HttpServletRequest request){
        def username = request.getParameter(this.usernameParam)
        def password = request.getParameter(this.passwordParam)
        new UsernamePasswordToken(username, password)
    }

    static class UsernamePasswordToken {
        String username;
        String password;

        UsernamePasswordToken(String username, String password) {
            this.username = username
            this.password = password
        }

        boolean isShouldParamAuth() {
            return StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)
        }
    }
}