package com.github.gateway.provider.properties;

import com.github.gateway.authc.exception.AuthenticationException;
import com.github.gateway.authc.token.CcAppSignatureToken;
import com.github.gateway.provider.SignParamAuthenticatingProvider;
import com.github.gateway.support.property.AppProperties;

/**
 * 所有参数全签名
 * TODO 是否需要抽象出一个appId和appKey的获取方式，使用注入式，而不是继承？？？
 */
public class PropertySignatureParamProvider extends SignParamAuthenticatingProvider {


    @Override
    protected CcPrincipal getCcPrincipal(CcAppSignatureToken token) throws AuthenticationException {
        String secret = AppProperties.getSecret(token.getKey());
        String customer = AppProperties.getCustomer(token.getKey());
        return new CcPrincipal(token.getKey(), secret, customer);
    }
}
