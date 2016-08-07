package com.github.api.gateway.authc.authenticator;

import com.github.api.gateway.authc.AuthenticationInfo;
import com.github.api.gateway.authc.AuthenticationListener;
import com.github.api.gateway.authc.AuthenticationToken;
import com.github.api.gateway.authc.Authenticator;
import com.github.api.gateway.authc.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chongdi.yang on 2016/8/7.
 */
public abstract class AbstractAuthenticator implements Authenticator {

    private static final Logger log = LoggerFactory.getLogger(AbstractAuthenticator.class);

    private Collection<AuthenticationListener> listeners;


    /**
     * Construct
     */
    public AbstractAuthenticator() {
        listeners = new ArrayList<AuthenticationListener>();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public Collection<AuthenticationListener> getAuthenticationListeners() {
        return this.listeners;
    }


    @SuppressWarnings({"UnusedDeclaration"})
    public void setAuthenticationListeners(Collection<AuthenticationListener> listeners) {
        if (listeners == null) {
            this.listeners = new ArrayList<AuthenticationListener>();
        } else {
            this.listeners = listeners;
        }
    }


    /*-------------------------------------------
    |               M E T H O D S               |
    ============================================*/

    protected void notifySuccess(Object in, List<AuthenticationToken> tokens, AuthenticationInfo info) {
        for (AuthenticationListener listener : this.listeners) {
            listener.onSuccess(tokens, info);
        }
    }


    protected void notifyFailure(Object in, List<AuthenticationToken> tokens, AuthenticationException ae) {
        for (AuthenticationListener listener : this.listeners) {
            listener.onFailure(tokens, ae);
        }
    }


    public AuthenticationInfo authenticate(Object any) throws AuthenticationException {
        log.trace("Authentication attempt received for T? [{}]", any.getClass());
        AuthenticationInfo info = null;
        List<AuthenticationToken> tokens = new LinkedList<AuthenticationToken>();
        try {
            info = doAuthenticate(any, tokens);
            if (info == null) {
                String msg = "No account information found for input param [" + any.getClass() + "] authentication token [" + tokens + "] by this " +
                        "Authenticator instance.  Please check that it is configured correctly.";
                throw new AuthenticationException(msg);
            }
        } catch (Throwable t) {
            AuthenticationException ae = null;
           if(t instanceof AuthenticationException) {
               ae = (AuthenticationException) t;
           } else {
               String msg = "authenticate fail by input param [" + any.getClass() + "], tokens [ " + tokens + " ] throw an unexpected error";
               ae = new AuthenticationException(msg, t);
           }

            try {
                notifyFailure(any, tokens, ae);
            } catch (Throwable t2) {
                if (log.isWarnEnabled()) {
                    String msg = "Unable to send notification for failed authentication attempt - listener error?.  " +
                            "Please check your AuthenticationListener implementation(s)";
                    log.warn(msg, t2);
                }
            }
            throw ae;
        }
        notifySuccess(any, tokens, info);
        return info;

    }

    protected abstract AuthenticationInfo doAuthenticate(Object any, List<AuthenticationToken> tokens) throws Throwable;
}
