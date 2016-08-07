package com.github.api.gateway.provider;

import com.github.api.gateway.authc.AuthenticationInfo;
import com.github.api.gateway.authc.AuthenticationToken;
import com.github.api.gateway.authc.credential.CredentialsMatcher;
import com.github.api.gateway.authc.exception.AccountException;
import com.github.api.gateway.authc.exception.AuthenticationException;
import com.github.api.gateway.authc.exception.ExpiredTimestampException;
import com.github.api.gateway.authc.exception.UnknownAppKeyException;
import com.github.api.gateway.authc.info.SimpleAuthenticationInfo;
import com.github.api.gateway.authc.token.CcAppSignatureToken;
import com.github.api.gateway.provider.producer.UsernamePasswordTokenProducer;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.math.LongRange;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class CcSignatureAuthenticatingProvider extends AuthenticatingProvider {

    public static final int FIVE_MINUTE_SECONDS = 5 * 60;

    private int timeRange = FIVE_MINUTE_SECONDS; // 秒为单位

    public CcSignatureAuthenticatingProvider() {
        super(new UsernamePasswordTokenProducer());
    }

    public CcSignatureAuthenticatingProvider(CredentialsMatcher credentialsMatcher) {
        super(new UsernamePasswordTokenProducer(), credentialsMatcher);
    }

    private void checkTimeRange(Long timestamp) throws ExpiredTimestampException {
        if(timestamp == null) {
            throw new ExpiredTimestampException();
        }
        //  get current time millis then convert to seconds
        Long current = System.currentTimeMillis() / 1000;
        LongRange range = new LongRange(current - timeRange, current + timeRange);
        if(!range.containsLong(timestamp)) {
            throw new ExpiredTimestampException();
        }
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        CcAppSignatureToken ccToken = (CcAppSignatureToken) token;
        checkTimeRange(ccToken.getTimestamp());
        String key = ccToken.getKey();
        if(key == null) {
            throw new AccountException("Null  key are not allowed.");
        }
        CcPrincipal principal = getCcPrincipal(ccToken);
        if (principal == null) {
            throw new UnknownAppKeyException("No app secret found for user [" + key + "]");
        }

        String signature = sign(ccToken, principal);

        return new SimpleAuthenticationInfo(principal, signature, getName());
    }


    protected String sign(CcAppSignatureToken token, CcPrincipal principal) {
        List<String> list = Arrays.asList(token.getRandom(), principal.getSecret(), token.getTimestamp().toString(), token.getKey());
        list = new ArrayList<String>(list);
        Collections.sort(list);
        return digest(list);
    }


    /**
     * digest List of the sortable str;
     * @param origin
     * @return
     */
    protected String digest(List<String> origin) {
        String dit = StringUtils.join(origin, ",");
        return DigestUtils.sha1Hex(dit);
    }

    /**
     * implements this for get Only principal, and add logs and so on
     * @param token
     * @return
     * @throws AuthenticationException
     */
    protected abstract CcPrincipal getCcPrincipal(CcAppSignatureToken token) throws AuthenticationException;


    /**
     * CC Principal 提供 key, secret and username <br/>
     * unmodify key, secret and username <br/>
     * this can be cached<br/>
     */
    public static class CcPrincipal {
        private String key;
        private String secret;
        private String username; //cusomer login name identity

        public CcPrincipal(String key, String secret, String username) {
            this.key = key;
            this.secret = secret;
            this.username = username;
        }

        public String getKey() {
            return key;
        }

        public String getSecret() {
            return secret;
        }

        public String getUsername() {
            return username;
        }
    }
}