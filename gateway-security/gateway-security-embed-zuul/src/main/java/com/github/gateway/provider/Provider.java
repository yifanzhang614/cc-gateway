package com.github.gateway.provider;

import com.github.gateway.authc.AuthenticationInfo;
import com.github.gateway.authc.AuthenticationToken;
import com.github.gateway.authc.exception.AuthenticationException;
import com.github.gateway.provider.producer.AuthenticationTokenProducer;

/**
 * Created by chongdi.yang on 2016/8/5.
 */
public interface Provider extends NameCapable, Nameable{

//    boolean supports(AuthenticationToken token);

    AuthenticationTokenProducer getAuthenticationTokenProducer();


    AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException;

}
