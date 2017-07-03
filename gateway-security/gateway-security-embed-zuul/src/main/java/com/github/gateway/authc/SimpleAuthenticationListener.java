package com.github.gateway.authc;

import com.github.gateway.authc.exception.AuthenticationException;

import java.util.List;

/**
 * Created by chongdi.yang on 2016/8/7.
 */
public class SimpleAuthenticationListener implements AuthenticationListener {

    @Override
    public void onFailure(List<AuthenticationToken> tokens, AuthenticationException ae) {

    }

    @Override
    public void onSuccess(List<AuthenticationToken> tokens, AuthenticationInfo info) {

    }
}
