import com.fasterxml.jackson.databind.ObjectMapper
import com.github.api.gateway.authc.exception.*
import com.github.api.gateway.filters.ZuulFilterType
import com.github.api.gateway.support.response.ResponseErrorConstant
import com.github.api.gateway.support.response.ResponseWrapper
import com.netflix.config.DynamicPropertyFactory
import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext

import javax.servlet.http.HttpServletResponse

/**
 * 认证出错处理过滤器，处理所有认证出错的信息。
 */
class AuthenticationHandlerFilter extends ZuulFilter{

    @Override
    String filterType() {
        return ZuulFilterType.ERROR.type
    }

    @Override
    int filterOrder() {
        return 1
    }

    @Override
    boolean shouldFilter() {
        return DynamicPropertyFactory.getInstance().getBooleanProperty("authticate", true)
    }

    @Override
    Object run() {
        RequestContext ctx = RequestContext.getCurrentContext()
        ResponseWrapper rw = null
        Throwable ex = ctx.getThrowable()
        if(ex != null) {
            Throwable t = ex.getCause();
            if(t instanceof ExpiredTimestampException) {
                rw = ResponseWrapper.error(ResponseErrorConstant.TIMESTAMP_EXPIRED)
            } else if(t instanceof UnknownAppKeyException) {
                rw = ResponseWrapper.error(ResponseErrorConstant.UNKNOWN_APPID)
            } else if(t instanceof IncorrectSignatureException) {
                rw = ResponseWrapper.error(ResponseErrorConstant.INCORRECT_SIGNATURE)
            } else if(t instanceof UnknownAccountException) {
                rw = ResponseWrapper.error(ResponseErrorConstant.UNKNOWN_ACCOUNT)
            } else if(t instanceof IncorrectCredentialsException) {
                rw = ResponseWrapper.error(ResponseErrorConstant.INCORRECT_CREDENTIAL)
            } else if(t instanceof AuthenticationException) {
                rw = ResponseWrapper.error(ResponseErrorConstant.AUTHENTICATE_FAIL)
            }
            if(rw != null) {
                ctx.setResponseBody(new ObjectMapper().writeValueAsString(rw))
                setContentType(ctx.getResponse())
            }
        }
        return null
    }

    void setContentType(HttpServletResponse response) {
        response.setContentType("application/json; charset=utf-8")
    }
}