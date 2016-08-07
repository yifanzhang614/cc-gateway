package com.github.api.gateway.authc;

/**
 * Created by chongdi.yang on 2016/8/6.
 */
public interface MergableAuthenticationInfo extends AuthenticationInfo {

    void merge(AuthenticationInfo info);
}
