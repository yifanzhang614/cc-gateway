package com.github.api.gateway.provider.producer;

import com.github.api.gateway.authc.AuthenticationToken;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by chongdi.yang on 2016/8/7.
 */
public class UsernamePasswordTokenProducer implements AuthenticationTokenProducer<HttpServletRequest> {
    @Override
    public AuthenticationToken produce(HttpServletRequest request) {
        return null;
    }
}
