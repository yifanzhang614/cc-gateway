package com.github.api.gateway.authc;

import java.io.Serializable;

/**
 * Created by chongdi.yang on 2016/8/5.
 */
public interface AuthenticationInfo extends Serializable {
    /**
     * Create Principal for username or appId and so on
     * */
    PrincipalCollection getPrincipals();


    /**
     * Create Credentials
     * */
    Object getCredentials();
}
