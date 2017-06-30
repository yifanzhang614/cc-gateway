package com.github.api.gateway.provider.properties;

import com.github.api.gateway.authc.AuthenticationToken;
import com.github.api.gateway.authc.exception.AuthenticationException;
import com.github.api.gateway.authc.token.CcAppSignatureToken;
import com.github.api.gateway.provider.CcSignatureAuthenticatingProvider;
import com.github.api.gateway.provider.producer.AuthenticationTokenProducer;
import com.github.api.gateway.provider.producer.CcSignatureTokenProducer;
import com.github.api.gateway.support.property.AppProperties;

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
