package com.github.api.gateway.authc.authenticator;

import com.github.api.gateway.authc.AuthenticationInfo;
import com.github.api.gateway.authc.AuthenticationToken;
import com.github.api.gateway.authc.exception.AuthenticationException;
import com.github.api.gateway.provider.Provider;

import java.util.Collection;

/**
 * Created by chongdi.yang on 2016/8/6.
 */
public class FirstSuccessfulReturnStrategy extends AbstractAuthenticationStrategy {

    @Override
    public AuthenticationInfo beforeAllAttempts(Collection<? extends Provider> providers) throws AuthenticationException {
        return null;
    }

    @Override
    public boolean isNextProvider(Provider provider, AuthenticationToken token, AuthenticationInfo aggregateInfo, Throwable t) throws Throwable {
        if(aggregateInfo != null && aggregateInfo.getPrincipals() != null && !aggregateInfo.getPrincipals().isEmpty()) {
            return false;
        }
        return super.isNextProvider(provider, token, aggregateInfo, t);
    }
}
