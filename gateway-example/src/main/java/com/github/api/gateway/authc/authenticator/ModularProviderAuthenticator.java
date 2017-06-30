package com.github.api.gateway.authc.authenticator;

import com.github.api.gateway.authc.AuthenticationInfo;
import com.github.api.gateway.authc.AuthenticationToken;
import com.github.api.gateway.authc.exception.AuthenticationException;
import com.github.api.gateway.authc.exception.UnknownAccountException;
import com.github.api.gateway.authc.exception.UnsupportedProducerException;
import com.github.api.gateway.provider.Provider;
import com.github.api.gateway.provider.producer.AuthenticationTokenProducer;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by chongdi.yang on 2016/8/5.
 */
public class ModularProviderAuthenticator extends AbstractAuthenticator {

    private static final Logger log = LoggerFactory.getLogger(ModularProviderAuthenticator.class);

    private Collection<Provider> providers = new ArrayList<Provider>();

    /**
     * The authentication strategy to use during authentication attempts, defaults to a
     */
    private AuthenticationStrategy authenticationStrategy;

    public ModularProviderAuthenticator() {
        authenticationStrategy = new FirstAuthenticationThenReturnStrategy();
    }

    public Collection<Provider> getProviders() {
        return providers;
    }

    /**
     * Set multipart Provider
     * @param providers
     */
    public void setProviders(Collection<Provider> providers) {
        this.providers = providers;
    }

    public AuthenticationStrategy getAuthenticationStrategy() {
        return authenticationStrategy;
    }

    /**
     * set AuthenticationStrategy for multipart provider
     * For example :
     * 1. 所有认证提供者只需要有一个通过则终止后续校验，返回第一个成功的，校验通过；否则不通过
     * 2. 所有认证提供者都必须通过，否则认证不通过
     * 3. 所有认证都走一遍，只要有一个通过，那么就通过
     * 4.
     * @param authenticationStrategy
     */
    public void setAuthenticationStrategy(AuthenticationStrategy authenticationStrategy) {
        this.authenticationStrategy = authenticationStrategy;
    }


    protected void assertProvidersConfigured() throws IllegalStateException {
        Collection<Provider> providers = getProviders();
        if (CollectionUtils.isEmpty(providers)) {
            String msg = "Configuration error:  No providers have been configured!  One or more realms must be " +
                    "present to execute an authentication attempt.";
            throw new IllegalStateException(msg);
        }
    }


    @Override
    protected AuthenticationInfo doAuthenticate(Object any, List<AuthenticationToken> tokens) throws Throwable {
        assertProvidersConfigured();
        Collection<Provider> providers = getProviders();
        if (providers.size() == 1) {
            return singleProviderAuthentication(providers.iterator().next(), any, tokens);
        } else {
            return multiProviderAuthentication(providers, any, tokens);
        }
    }

    protected AuthenticationInfo singleProviderAuthentication(Provider provider, Object any, List<AuthenticationToken> tokens) {
        AuthenticationInfo info = null;
        AuthenticationTokenProducer producer = provider.getAuthenticationTokenProducer();
        if (!producer.supports(any)) {

            String msg = "Provider [" + provider + "] with Producer [" + producer + "] does not support Object [" +
                    any.getClass() + "].  Please ensure that the appropriate Provider implementation is " +
                    "configured correctly or that the realm accepts AuthenticationTokens of this type.";
            throw new UnsupportedProducerException(msg);
        } else {
            AuthenticationToken token = producer.produce(any);
            tokens.add(token);
            info = provider.getAuthenticationInfo(token);
            if (info == null) {
                String msg = "Provider [" + provider + "] was unable to find account data for the " +
                        "submitted AuthenticationToken [" + token + "].";
                throw new UnknownAccountException(msg);
            }
        }
        return info;
    }


    protected AuthenticationInfo multiProviderAuthentication(Collection<Provider> providers, Object any, List<AuthenticationToken> tokens) throws Throwable {

        AuthenticationStrategy strategy = getAuthenticationStrategy();

        AuthenticationInfo aggregate = strategy.beforeAllAttempts(providers);

        if (log.isTraceEnabled()) {
            log.trace("Iterating through {} realms for strategy authentication", providers.size());
        }

        for (Provider provider : providers) {
            AuthenticationTokenProducer producer = provider.getAuthenticationTokenProducer();
            if (producer.supports(any)) {
                AuthenticationToken token = producer.produce(any);
                tokens.add(token);
                aggregate = strategy.beforeAttempt(provider, token, aggregate);

                log.trace("Attempting to authenticate token [{}] using provider [{}]", token, provider);

                AuthenticationInfo info = null;
                Throwable t = null;
                try {
                    info = provider.getAuthenticationInfo(token);
                } catch (Throwable throwable) {
                    t = throwable;
                    if (log.isDebugEnabled()) {
                        String msg = "Provider [" + provider + "] threw an exception during a multi-provider authentication attempt:";
                        log.debug(msg, t);
                    }
                }

                aggregate = strategy.afterAttempt(provider, token, info, aggregate, t);

                // continue to execute next provider
                if(!strategy.isNextProvider(provider, token, aggregate, t)) {
                    break;
                }

            } else {
                log.debug("Provider [{}] with {} does not support Object any {}.  Skipping Provider.", provider, producer, any);
            }
        }

        aggregate = strategy.afterAllAttempts(aggregate);
        return aggregate;
    }
}
