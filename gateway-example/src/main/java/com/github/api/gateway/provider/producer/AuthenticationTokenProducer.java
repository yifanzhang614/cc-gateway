package com.github.api.gateway.provider.producer;

import com.github.api.gateway.authc.AuthenticationToken;

/**
 * Created by chongdi.yang on 2016/8/7.
 */
public interface AuthenticationTokenProducer {

    boolean supports(Object any);

    AuthenticationToken produce(Object any);

}
