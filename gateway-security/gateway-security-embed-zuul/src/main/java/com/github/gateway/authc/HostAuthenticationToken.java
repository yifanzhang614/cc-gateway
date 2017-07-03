package com.github.gateway.authc;

/**
 * Created by chongdi.yang on 2016/8/7.
 */
public interface HostAuthenticationToken extends AuthenticationToken {

    String getHost();
}
