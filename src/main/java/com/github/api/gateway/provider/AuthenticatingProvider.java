package com.github.api.gateway.provider;

import com.github.api.gateway.authc.AuthenticationInfo;
import com.github.api.gateway.authc.AuthenticationToken;
import com.github.api.gateway.authc.PrincipalCollection;
import com.github.api.gateway.authc.credential.AllowAllCredentialsMatcher;
import com.github.api.gateway.authc.credential.CredentialsMatcher;
import com.github.api.gateway.authc.credential.SimpleCredentialsMatcher;
import com.github.api.gateway.authc.exception.AuthenticationException;
import com.github.api.gateway.authc.exception.IncorrectCredentialsException;
import com.github.api.gateway.provider.producer.AuthenticationTokenProducer;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chongdi.yang on 2016/8/6.
 */
public abstract class AuthenticatingProvider implements Provider {

    private static final Logger log = LoggerFactory.getLogger(AuthenticatingProvider.class);

    private CredentialsMatcher credentialsMatcher;

    private AuthenticationTokenProducer authenticationTokenProducer;


    private String name;

    private static final AtomicInteger INSTANCE_COUNT = new AtomicInteger();

    public AuthenticatingProvider(AuthenticationTokenProducer producer) {
        this(producer, new SimpleCredentialsMatcher());
    }

    public AuthenticatingProvider(AuthenticationTokenProducer producer, CredentialsMatcher credentialsMatcher) {
        if(credentialsMatcher != null) {
            setCredentialsMatcher(credentialsMatcher);
        }
        setAuthenticationTokenProducer(producer);
        this.name = getClass().getName() + "_" + INSTANCE_COUNT.getAndIncrement();
    }

    public CredentialsMatcher getCredentialsMatcher() {
        return credentialsMatcher;
    }

    public void setCredentialsMatcher(CredentialsMatcher credentialsMatcher) {
        this.credentialsMatcher = credentialsMatcher;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public AuthenticationTokenProducer getAuthenticationTokenProducer() {
        return authenticationTokenProducer;
    }


    public void setAuthenticationTokenProducer(AuthenticationTokenProducer authenticationTokenProducer) {
        this.authenticationTokenProducer = authenticationTokenProducer;
    }

    protected Object getAvailablePrincipal(PrincipalCollection principals) {
        Object primary = null;
        if (!(principals == null || principals.isEmpty())) {
            Collection thisPrincipals = principals.fromProvider(getName());
            if (!CollectionUtils.isEmpty(thisPrincipals)) {
                primary = thisPrincipals.iterator().next();
            } else {
                //no principals attributed to this particular realm.  Fall back to the 'master' primary:
                primary = principals.getPrimaryPrincipal();
            }
        }

        return primary;
    }


    public final AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

            //otherwise not cached, perform the lookup:
        AuthenticationInfo info = doGetAuthenticationInfo(token);
        log.debug("Looked up AuthenticationInfo [{}] from doGetAuthenticationInfo", info);
        if (info != null) {
            assertCredentialsMatch(token, info);
        } else {
            log.debug("No AuthenticationInfo found for submitted AuthenticationToken [{}].  Returning null.", token);
        }

        return info;
    }

    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
        CredentialsMatcher cm = getCredentialsMatcher();
        if (cm != null) {
            if (!cm.doCredentialsMatch(token, info)) {
                //not successful - throw an exception to indicate this:
                String msg = "Submitted credentials for token [" + token + "] did not match the expected credentials.";
                throw new IncorrectCredentialsException(msg);
            }
        } else {
            throw new AuthenticationException("A CredentialsMatcher must be configured in order to verify " +
                    "credentials during authentication.  If you do not wish for credentials to be examined, you " +
                    "can configure an " + AllowAllCredentialsMatcher.class.getName() + " instance.");
        }
    }

    protected abstract AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException;
}
