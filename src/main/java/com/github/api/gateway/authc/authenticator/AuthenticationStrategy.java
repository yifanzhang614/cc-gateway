package com.github.api.gateway.authc.authenticator;

import com.github.api.gateway.authc.AuthenticationInfo;
import com.github.api.gateway.authc.AuthenticationToken;
import com.github.api.gateway.authc.exception.AuthenticationException;
import com.github.api.gateway.provider.Provider;

import java.util.Collection;

/**
 * Created by chongdi.yang on 2016/8/5.
 */
public interface AuthenticationStrategy {

    /**
     * Returns {@code true} if the aggregate should execute next provider.
     */
    boolean isNextProvider(Provider provider, AuthenticationToken token, AuthenticationInfo aggregateInfo, Throwable t) throws Throwable;


    AuthenticationInfo beforeAllAttempts(Collection<? extends Provider> providers) throws AuthenticationException;

    /**
     * Method invoked by the ModularAuthenticator just prior to the realm being consulted for account data,
     * allowing pre-authentication-attempt logic for that realm only.
     *
     * <p>This method returns an {@code AuthenticationInfo} object that will be used for further interaction with realms.  Most
     * implementations will merely return the {@code aggregate} method argument if they don't have a need to
     * manipulate it.
     *
     * @param provider     the realm that will be consulted for {@code AuthenticationInfo} for the specified {@code token}.
     * @param token     the {@code AuthenticationToken} submitted for the subject attempting system log-in.
     * @param aggregate the aggregated AuthenticationInfo object being used across the multi-realm authentication attempt
     * @return the AuthenticationInfo object that will be presented to further realms in the authentication process - returning
     *         the {@code aggregate} method argument is the normal case if no special action needs to be taken.
     * @throws  AuthenticationException
     *          an exception thrown by the Strategy implementation if it wishes the login
     *          process for the associated subject (user) to stop immediately.
     */
    AuthenticationInfo beforeAttempt(Provider provider, AuthenticationToken token, AuthenticationInfo aggregate) throws AuthenticationException;

    /**
     * Method invoked by the ModularAuthenticator just after the given realm has been consulted for authentication,
     * allowing post-authentication-attempt logic for that realm only.
     *
     * <p>This method returns an {@code AuthenticationInfo} object that will be used for further interaction with realms.  Most
     * implementations will merge the {@code singleRealmInfo} into the {@code aggregateInfo} and
     * just return the {@code aggregateInfo} for continued use throughout the authentication process.</p>
     *
     * @param provider           the realm that was just consulted for {@code AuthenticationInfo} for the given {@code token}.
     * @param token           the {@code AuthenticationToken} submitted for the subject attempting system log-in.
     * @param aggregateInfo   the aggregate info representing all realms in a multi-realm environment.
     * @param t               the Throwable thrown by the Realm during the attempt, or {@code null} if the method returned normally.
     * @return the AuthenticationInfo object that will be presented to further realms in the authentication process - returning
     *         the {@code aggregateAccount} method argument is the normal case if no special action needs to be taken.
     * @throws Throwable an exception thrown by the Strategy implementation if it wishes the login process
     *                                 for the associated subject (user) to stop immediately.
     */
    AuthenticationInfo afterAttempt(Provider provider, AuthenticationToken token, AuthenticationInfo singleInfo, AuthenticationInfo aggregateInfo, Throwable t)
            throws Throwable;


    AuthenticationInfo afterAllAttempts(AuthenticationInfo aggregate) throws AuthenticationException;
}
