package com.github.gateway.provider.producer;

import com.github.gateway.authc.AuthenticationToken;

/**
 * Created by chongdi.yang on 2016/8/7.
 */
public abstract class AbstractAuthenticationTokenProducer implements AuthenticationTokenProducer {

    @Override
    public boolean supports(Object any) {
        return false;
    }

    @Override
    public AuthenticationToken produce(Object any) {
        return null;
    }


    @Override
    public String toString() {
        return this.getClass().getName();
    }
}
