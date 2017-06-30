package com.github.gateway.authc;

import com.github.gateway.authc.exception.AuthenticationException;

import java.util.List;

/**
 * This the Listener for authenticate, and notify。if don't want to implements all.
 *
 * can extends {@link com.github.gateway.authc.listener.SimpleAuthenticationListener}
 */
public interface AuthenticationListener {


    void onSuccess(List<AuthenticationToken> tokens, AuthenticationInfo info);


    void onFailure(List<AuthenticationToken> tokens, AuthenticationException ae);
}
