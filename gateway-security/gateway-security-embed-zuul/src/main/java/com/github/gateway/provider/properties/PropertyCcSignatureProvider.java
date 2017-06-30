package com.github.gateway.provider.properties;

import com.github.gateway.authc.exception.AuthenticationException;
import com.github.gateway.authc.token.CcAppSignatureToken;
import com.github.gateway.provider.CcSignatureAuthenticatingProvider;
import com.github.gateway.support.property.AppProperties;

/**
 * Created by chongdi.yang on 2016/8/7.
 */
public class PropertyCcSignatureProvider extends CcSignatureAuthenticatingProvider {


    @Override
    protected CcPrincipal getCcPrincipal(CcAppSignatureToken token) throws AuthenticationException {
        String secret = AppProperties.getSecret(token.getKey());
        String customer = AppProperties.getCustomer(token.getKey());
        return new CcPrincipal(token.getKey(), secret, customer);
    }
}
