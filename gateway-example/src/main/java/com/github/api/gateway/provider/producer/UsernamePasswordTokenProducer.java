package com.github.api.gateway.provider.producer;

import com.github.api.gateway.authc.AuthenticationToken;
import com.github.api.gateway.authc.token.UsernamePasswordToken;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by chongdi.yang on 2016/8/7.
 */
public class UsernamePasswordTokenProducer extends AbstractAuthenticationTokenProducer {
    public static  final String USERNAME = "api_user";
    public static  final String PASSWORD = "api_key";

    private String usernameParam = USERNAME;
    private String passwordParam = PASSWORD;



    @Override
    public boolean supports(Object any) {
        if(any == null) {
            return false;
        }
        if(any instanceof HttpServletRequest) {
            HttpServletRequest request = (HttpServletRequest) any;
            if(request.getParameter(this.usernameParam) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AuthenticationToken produce(Object any) {
        HttpServletRequest request = (HttpServletRequest) any;
        String username = request.getParameter(this.usernameParam);
        String password = request.getParameter(this.passwordParam);
        return new UsernamePasswordToken(username, password, request.getRemoteHost());
    }

    public String getUsernameParam() {
        return usernameParam;
    }

    public void setUsernameParam(String usernameParam) {
        this.usernameParam = usernameParam;
    }

    public String getPasswordParam() {
        return passwordParam;
    }

    public void setPasswordParam(String passwordParam) {
        this.passwordParam = passwordParam;
    }
}
