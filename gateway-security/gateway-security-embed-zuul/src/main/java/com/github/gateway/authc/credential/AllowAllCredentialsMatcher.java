package com.github.gateway.authc.credential;

import com.github.gateway.authc.AuthenticationInfo;
import com.github.gateway.authc.AuthenticationToken;

/**
 * Created by chongdi.yang on 2016/8/7.
 */
public class AllowAllCredentialsMatcher implements CredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        return true;
    }
}
