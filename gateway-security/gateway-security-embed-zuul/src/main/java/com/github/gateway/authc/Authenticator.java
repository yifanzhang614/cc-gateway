package com.github.gateway.authc;

import com.github.gateway.authc.exception.AuthenticationException;

/**
 * Created by chongdi.yang on 2016/8/5.
 */
public interface Authenticator {

    AuthenticationInfo authenticate(Object any) throws AuthenticationException;

}
