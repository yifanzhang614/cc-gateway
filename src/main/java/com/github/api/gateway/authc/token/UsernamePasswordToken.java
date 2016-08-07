package com.github.api.gateway.authc.token;

import com.github.api.gateway.authc.AuthenticationToken;

/**
 * Created by chongdi.yang on 2016/8/6.
 */
public class UsernamePasswordToken implements AuthenticationToken {
    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
