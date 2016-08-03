import com.fasterxml.jackson.databind.ObjectMapper
import com.github.api.gateway.exception.authentication.AuthenticationRuntimeException
import com.github.api.gateway.exception.authentication.IncorrectCredentialException
import com.github.api.gateway.exception.authentication.IncorrectSignatureException
import com.github.api.gateway.exception.authentication.IncorrectTimestampRangeException
import com.github.api.gateway.exception.authentication.UnknownAccountException
import com.github.api.gateway.exception.authentication.UnknownAppIdException
import com.github.api.gateway.filters.ZuulFilterType
import com.github.api.gateway.support.auth.AuthenticatingResult
import com.github.api.gateway.support.response.ResponseErrorConstant
import com.github.api.gateway.support.response.ResponseWrapper
import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext

import javax.servlet.http.HttpServletResponse

/**
 * Created by chdyan on 16/8/3.
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
        return true
    }

    @Override
    Object run() {
        RequestContext ctx = RequestContext.getCurrentContext()
        ResponseWrapper rw = null
        Throwable ex = ctx.getThrowable()
        if(ex != null) {
            Throwable t = ex.getCause();
            if(t instanceof IncorrectTimestampRangeException) {
                rw = ResponseWrapper.error(ResponseErrorConstant.TIMESTAMP_EXPIRED)
            } else if(t instanceof UnknownAppIdException) {
                rw = ResponseWrapper.error(ResponseErrorConstant.UNKNOWN_APPID)
            } else if(t instanceof IncorrectSignatureException) {
                rw = ResponseWrapper.error(ResponseErrorConstant.INCORRECT_SIGNATURE)
            } else if(t instanceof UnknownAccountException) {
                rw = ResponseWrapper.error(ResponseErrorConstant.UNKNOWN_ACCOUNT)
            } else if(t instanceof IncorrectCredentialException) {
                rw = ResponseWrapper.error(ResponseErrorConstant.INCORRECT_CREDENTIAL)
            } else if(t instanceof AuthenticationRuntimeException) {
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