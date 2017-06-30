package com.github.api.gateway.filters.route.ribbon.util;

import com.netflix.client.IClient;
import com.netflix.client.IClientConfigAware;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * A factory that creates client, load balancer and client configuration instances.
 * It creates a Spring ApplicationContext per client name, and extracts the beans
 * that it needs from there.
 *
 * Created by yifanzhang.
 */
public class SpringClientFactory extends NamedContextFactory<RibbonClientSpecification> {
    public SpringClientFactory() {
        super(RibbonClientConfiguration.class,"ribbon","ribbon.client.name");
    }

    /**
     * Get the rest client associated with the name.
     * @throws RuntimeException if any error occurs
     */
    public <C extends IClient<?, ?>> C getClient(String name, Class<C> clientClass) {
        return getInstance(name, clientClass);
    }

    /**
     * Get the load balancer associated with the name.
     * @throws RuntimeException if any error occurs
     */
    public ILoadBalancer getLoadBalancer(String name) {
        return getInstance(name, ILoadBalancer.class);
    }

    /**
     * Get the client config associated with the name.
     * @throws RuntimeException if any error occurs
     */
    public IClientConfig getClientConfig(String name) {
        return getInstance(name, IClientConfig.class);
    }

    /**
     * Get the load balancer context associated with the name.
     * @throws RuntimeException if any error occurs
     */
    public RibbonLoadBalancerContext getLoadBalancerContext(String serviceId) {
        return getInstance(serviceId, RibbonLoadBalancerContext.class);
    }

    private <C> C instantiateWithConfig(AnnotationConfigApplicationContext context,
        Class<C> clazz, IClientConfig config) {
        C result = null;
        if (IClientConfigAware.class.isAssignableFrom(clazz)) {
            IClientConfigAware obj = (IClientConfigAware) BeanUtils.instantiate(clazz);
            obj.initWithNiwsConfig(config);
            @SuppressWarnings("unchecked")
            C value = (C) obj;
            result = value;
        }
        else {
            try {
                if (clazz.getConstructor(IClientConfig.class) != null) {
                    result = clazz.getConstructor(IClientConfig.class)
                        .newInstance(config);
                }
                else {
                    result = BeanUtils.instantiate(clazz);
                }
            }
            catch (Throwable ex) {
                // NOPMD
            }
        }
        context.getAutowireCapableBeanFactory().autowireBean(result);
        return result;
    }

    public <C> C getInstance(String name, Class<C> type) {
        C instance = super.getInstance(name, type);
        if (instance != null) {
            return instance;
        }
        IClientConfig config = getInstance(name, IClientConfig.class);
        return instantiateWithConfig(getContext(name), type, config);
    }
}
