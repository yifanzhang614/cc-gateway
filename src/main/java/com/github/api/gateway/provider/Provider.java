package com.github.api.gateway.provider;

import com.github.api.gateway.authc.AuthenticationInfo;
import com.github.api.gateway.authc.AuthenticationToken;
import com.github.api.gateway.authc.exception.AuthenticationException;
import com.github.api.gateway.provider.producer.AuthenticationTokenProducer;

/**
 * Created by chongdi.yang on 2016/8/5.
 */
public interface Provider extends NameCapable, Nameable{

//    boolean supports(AuthenticationToken token);

    AuthenticationTokenProducer getAuthenticationTokenProducer();


    AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException;

}
