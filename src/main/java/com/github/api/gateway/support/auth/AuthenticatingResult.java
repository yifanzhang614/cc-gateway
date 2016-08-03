package com.github.api.gateway.support.auth;

/**
 * Created by chdyan on 16/8/3.
 */
public class AuthenticatingResult {

    public static final String AUTHENTICATING_RESULT = AuthenticatingResult.class.getName() + ".result";

    private ExcuteState auth = ExcuteState.NOEXCUTE; // 默认校验不通过
    private ExcuteState usernamePasswordAuthState = ExcuteState.NOEXCUTE; // 默认校验不通过
    private ExcuteState headerSignatureAuthState = ExcuteState.NOEXCUTE; // 默认校验不通过

    public ExcuteState getAuth() {
        return auth;
    }

    public void setAuth(ExcuteState auth) {
        this.auth = auth;
    }

    public ExcuteState getHeaderSignatureAuthState() {
        return headerSignatureAuthState;
    }

    public void setHeaderSignatureAuthState(ExcuteState headerSignatureAuthState) {
        this.headerSignatureAuthState = headerSignatureAuthState;
    }

    public ExcuteState getUsernamePasswordAuthState() {
        return usernamePasswordAuthState;
    }

    public void setUsernamePasswordAuthState(ExcuteState usernamePasswordAuthState) {
        this.usernamePasswordAuthState = usernamePasswordAuthState;
    }

    public boolean authenticatePass() {
        if(getAuth().isExcute()) {
            return true;
        }
        if(getHeaderSignatureAuthState().isExcute()) {
            return true;
        }
        if(getUsernamePasswordAuthState().isExcute()) {
            return true;
        }
        return false;
    }
}
