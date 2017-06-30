package com.github.api.gateway.authc.authenticator;

import com.github.api.gateway.authc.AuthenticationInfo;
import com.github.api.gateway.authc.AuthenticationToken;
import com.github.api.gateway.authc.MergableAuthenticationInfo;
import com.github.api.gateway.authc.info.SimpleAuthenticationInfo;
import com.github.api.gateway.authc.exception.AuthenticationException;
import com.github.api.gateway.provider.Provider;

import java.util.Collection;

/**
 * Created by chongdi.yang on 2016/8/6.
 */
public class AbstractAuthenticationStrategy implements AuthenticationStrategy {

    @Override
    public boolean isNextProvider(Provider provider, AuthenticationToken token, AuthenticationInfo aggregateInfo, Throwable t) throws Throwable {
        return true;
    }

    public AuthenticationInfo beforeAllAttempts(Collection<? extends Provider> realms) throws AuthenticationException {
        return new SimpleAuthenticationInfo();
    }

    /**
     * Simply returns the <code>aggregate</code> method argument, without modification.
     */
    public AuthenticationInfo beforeAttempt(Provider realm, AuthenticationToken token, AuthenticationInfo aggregate) throws AuthenticationException {
        return aggregate;
    }

    /**
     * Base implementation that will aggregate the specified <code>singleRealmInfo</code> into the
     * <code>aggregateInfo</code> and then returns the aggregate.  Can be overridden by subclasses for custom behavior.
     */
    public AuthenticationInfo afterAttempt(Provider realm, AuthenticationToken token, AuthenticationInfo singleRealmInfo, AuthenticationInfo aggregateInfo, Throwable t) throws AuthenticationException {
        AuthenticationInfo info;
        if (singleRealmInfo == null) {
            info = aggregateInfo;
        } else {
            if (aggregateInfo == null) {
                info = singleRealmInfo;
            } else {
                info = merge(singleRealmInfo, aggregateInfo);
            }
        }

        return info;
    }


    protected AuthenticationInfo merge(AuthenticationInfo info, AuthenticationInfo aggregate) {
        if( aggregate instanceof MergableAuthenticationInfo) {
            ((MergableAuthenticationInfo)aggregate).merge(info);
            return aggregate;
        } else {
            throw new IllegalArgumentException( "Attempt to merge authentication info from multiple realms, but aggregate " +
                    "AuthenticationInfo is not of type MergableAuthenticationInfo." );
        }
    }

    /**
     * Simply returns the <code>aggregate</code> argument without modification.  Can be overridden for custom behavior.
     */
    public AuthenticationInfo afterAllAttempts( AuthenticationInfo aggregate) throws AuthenticationException {
        return aggregate;
    }
}
