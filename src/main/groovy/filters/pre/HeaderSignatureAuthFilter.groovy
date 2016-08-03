import com.github.api.gateway.exception.authentication.HeaderAuthenticationException
import com.github.api.gateway.exception.authentication.IncorrectSignatureException
import com.github.api.gateway.exception.authentication.IncorrectTimestampRangeException
import com.github.api.gateway.exception.authentication.UnknownAppIdException
import com.github.api.gateway.filters.ZuulFilterType
import com.github.api.gateway.support.auth.AuthenticatingResult
import com.github.api.gateway.support.auth.ExcuteState
import com.github.api.gateway.support.property.AppIdSecretProperties
import com.github.api.gateway.util.StringUtils
import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.lang.math.LongRange




import javax.servlet.http.HttpServletRequest

/**
 * Appid and AppSecret generate a signature
 * With validate signature is correct
 */
class HeaderSignatureAuthFilter extends ZuulFilter {


    static  final String HEADER_AUTH_PARAM = HeaderSignatureAuthFilter.name + ".requestParam";
    static  final String RANDOM = "X-CC-Auth-Nonce";
    static  final String SIGNATURE = "X-CC-Auth-Signature";
    static  final String TIMESTAMP = "X-CC-Auth-Timestamp";
    static  final String KEY = "X-CC-Auth-key";
    static final int FIVE_MINUTE_SECONDS = 5 * 60;

    private String randomParam = RANDOM;
    private String signatureParam = SIGNATURE;
    private String timestampParam = TIMESTAMP;
    private String keyParam = KEY;

    @Override
    String filterType() {
        return ZuulFilterType.PRE.getType();
    }

    @Override
    int filterOrder() {
        return 11;
    }

    @Override
    boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        AuthenticatingResult result = ctx.get(AuthenticatingResult.AUTHENTICATING_RESULT)
        if(!result.authenticatePass()) {
            RequestToken token = this.newRequestToken(ctx.getRequest())
            boolean isAuth = token.isHeaderAuth();
            if(isAuth) {
                result.setHeaderSignatureAuthState(ExcuteState.EXCUTE);
            }
            ctx.set(HEADER_AUTH_PARAM, token);
            return isAuth;
        }
        return false
    }

    @Override
    Object run() throws HeaderAuthenticationException {
        RequestContext ctx = RequestContext.getCurrentContext()
        RequestToken token = ctx.get(HEADER_AUTH_PARAM)
        //校验是否在five minute范围内
        isBetweenFiveMinite(token.getTimestamp())
        String secret = AppIdSecretProperties.getSecret(token.getKey())
        if(secret == null) {
            throw new UnknownAppIdException()
        }
        String customer = AppIdSecretProperties.getCustomer(token.getKey())
        RequestInfo info = new RequestInfo(token, secret)
        boolean b = info.isAuthSuccess()
        if(!b) {
            throw new IncorrectSignatureException()
        }
        return b
    }

    private boolean isBetweenFiveMinite(Long timestamp) throws IncorrectTimestampRangeException {
        //  get current time millis then convert to seconds
        Long current = System.currentTimeMillis() / 1000
        LongRange range = new LongRange(current - FIVE_MINUTE_SECONDS, current + FIVE_MINUTE_SECONDS);
        boolean b = range.containsLong(timestamp)
        if(!b) {
            throw new IncorrectTimestampRangeException()
        }
        return b
    }

    /**
     * generate a Request Token
     * @param request
     * @return
     */
    def newRequestToken(HttpServletRequest request) {
        def random = request.getHeader(this.randomParam)
        def signature = request.getHeader(this.signatureParam)
        def timestamp = request.getHeader(this.timestampParam)
        def key = request.getHeader(this.keyParam)
        new RequestToken(random, signature, timestamp, key)
    }

    static class RequestToken {
        String random;
        String signature;
        Long timestamp;  //五分钟内有效
        String key;

        RequestToken(RequestToken token) {
            this.random = token.random
            this.signature = token.signature
            this.timestamp = token.timestamp
            this.key = token.key
        }

        RequestToken(String random, String signature, Long timestamp, String key) {
            this.random = random
            this.signature = signature
            this.timestamp = timestamp
            this.key = key
        }

        /**
         * Whether use header authencication
         * @return
         */
        boolean isHeaderAuth() {
            return !(StringUtils.isEmpty(getRandom()) || StringUtils.isEmpty(getSignature()) || StringUtils.isEmpty(getKey()) || Objects.isNull(getTimestamp()));
        }

    }

    /**
     * Request Info to validate request Token is successful?
     */
    class RequestInfo extends  RequestToken {
        List<String> list; //组成list按默认字典排序
        String secret;
        private String generateSignature;


        RequestInfo(RequestToken token, String secret) {
            super(token)
            this.secret = secret
        }


        String getGenerateSignature() {
            if(generateSignature == null) {
                List<String> list = toSortedList()
                String noEncript = list.join("")
                generateSignature = DigestUtils.sha1(noEncript)
            }
            return generateSignature
        }


        List<String> toSortedList() {
            if(list == null) {
                list = [getRandom(), getSignature(), getTimestamp().toString(), getKey()]
                list = Collections.sort(list)
            }
            return list
        }

        boolean isAuthSuccess() {
            return org.apache.commons.lang.StringUtils.equals(this.getSignature(), this.getGenerateSignature());
        }
    }
}