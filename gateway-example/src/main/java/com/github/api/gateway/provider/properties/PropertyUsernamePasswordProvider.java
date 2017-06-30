package com.github.api.gateway.provider.properties;

import com.github.api.gateway.authc.token.UsernamePasswordToken;
import com.github.api.gateway.provider.UsernamePasswordProvider;
import com.github.api.gateway.support.property.UserProperties;

/**
 * Created by chongdi.yang on 2016/8/7.
 */
public class PropertyUsernamePasswordProvider extends UsernamePasswordProvider {
    @Override
    protected String getPassword(String username) {
        return UserProperties.get(username);
    }

    @Override
    protected Object getPrincipal(UsernamePasswordToken token, String password) {
        return token.getUsername();
    }
}
