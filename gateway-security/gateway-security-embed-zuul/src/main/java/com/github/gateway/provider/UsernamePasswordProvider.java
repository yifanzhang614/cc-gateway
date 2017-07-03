package com.github.gateway.provider;

import com.github.gateway.authc.AuthenticationInfo;
import com.github.gateway.authc.AuthenticationToken;
import com.github.gateway.authc.credential.CredentialsMatcher;
import com.github.gateway.authc.exception.AccountException;
import com.github.gateway.authc.exception.AuthenticationException;
import com.github.gateway.authc.exception.UnknownAccountException;
import com.github.gateway.authc.info.SimpleAuthenticationInfo;
import com.github.gateway.authc.token.UsernamePasswordToken;
import com.github.gateway.provider.producer.UsernamePasswordTokenProducer;

/**
 * Created by chongdi.yang on 2016/8/7.
 */
public abstract class UsernamePasswordProvider extends AuthenticatingProvider {

    public UsernamePasswordProvider() {
        super(new UsernamePasswordTokenProducer());
    }

    public UsernamePasswordProvider(CredentialsMatcher credentialsMatcher) {
        super(new UsernamePasswordTokenProducer(), credentialsMatcher);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        if(username == null) {
            throw new AccountException("Null username are not allowed.");
        }
        String password = getPassword(username);

        if (password == null) {
            throw new UnknownAccountException("No app secret found for user [" + username + "]");
        }
        return new SimpleAuthenticationInfo(getPrincipal(upToken, password), password, getName());
    }


    protected abstract String getPassword(String username);

    protected abstract Object getPrincipal(UsernamePasswordToken token, String password);
}
