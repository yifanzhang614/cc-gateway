package com.github.api.gateway.authc;

import java.io.Serializable;

/**
 * Created by chongdi.yang on 2016/8/4.
 */
public interface AuthenticationToken extends Serializable {

    /**
     * Create Principal for username or appId and so on
     * */
    Object getPrincipal();


    /**
     * Create Credentials
     * */
    Object getCredentials();
}
