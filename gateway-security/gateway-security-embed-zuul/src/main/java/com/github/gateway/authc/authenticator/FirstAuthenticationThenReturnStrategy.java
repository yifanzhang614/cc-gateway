package com.github.gateway.authc.authenticator;

import com.github.gateway.authc.AuthenticationInfo;
import com.github.gateway.authc.AuthenticationToken;
import com.github.gateway.authc.exception.AuthenticationException;
import com.github.gateway.provider.Provider;

import java.util.Collection;

/**
 * Created by chongdi.yang on 2016/8/8.
 */
public class FirstAuthenticationThenReturnStrategy extends AbstractAuthenticationStrategy {

    @Override
    public boolean isNextProvider(Provider provider, AuthenticationToken token, AuthenticationInfo aggregateInfo, Throwable t) throws Throwable {
        if(aggregateInfo != null && aggregateInfo.getPrincipals() != null && !aggregateInfo.getPrincipals().isEmpty()) {
            return false;
        }
        if(token != null) {
            throw  t;
        }
        return true;
    }

    @Override
    public AuthenticationInfo beforeAllAttempts(Collection<? extends Provider> realms) throws AuthenticationException {
        return null;
    }
}
