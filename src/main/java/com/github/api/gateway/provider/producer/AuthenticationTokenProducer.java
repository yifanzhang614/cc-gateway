package com.github.api.gateway.provider.producer;

import com.github.api.gateway.authc.AuthenticationInfo;
import com.github.api.gateway.authc.AuthenticationToken;
import com.github.api.gateway.authc.credential.CredentialsMatcher;
import com.github.api.gateway.authc.exception.AccountException;
import com.github.api.gateway.authc.exception.AuthenticationException;
import com.github.api.gateway.authc.exception.UnknownAppKeyException;
import com.github.api.gateway.authc.info.SimpleAuthenticationInfo;
import com.github.api.gateway.authc.token.CcAppSignatureToken;
import com.github.api.gateway.provider.AuthenticatingProvider;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by chongdi.yang on 2016/8/7.
 */
public interface AuthenticationTokenProducer<T> {

    AuthenticationToken produce(T t);

    /**
     * Created by chongdi.yang on 2016/8/7.
     */
    abstract class CcSignatureProvider extends AuthenticatingProvider {

        /**
         * 设置默认比较器
         */
        public CcSignatureProvider() {
            setAuthenticationTokenClass(CcAppSignatureToken.class);
        }

        public CcSignatureProvider(CredentialsMatcher credentialsMatcher) {
            super(credentialsMatcher);
            setAuthenticationTokenClass(CcAppSignatureToken.class);
        }

        /**
         * Override supports add this only feature.
         * @param token
         * @return
         */
        @Override
        public boolean supports(AuthenticationToken token) {
            boolean b = super.supports(token);
            if(b) {
                return ((CcAppSignatureToken) token).needAuthenticate();
            }
            return b;
        }

        @Override
        protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
            CcAppSignatureToken ccToken = (CcAppSignatureToken) token;
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
}
