package com.github.api.gateway.authc.listener;

import com.github.api.gateway.authc.AuthenticationInfo;
import com.github.api.gateway.authc.AuthenticationListener;
import com.github.api.gateway.authc.AuthenticationToken;
import com.github.api.gateway.authc.exception.AuthenticationException;

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
