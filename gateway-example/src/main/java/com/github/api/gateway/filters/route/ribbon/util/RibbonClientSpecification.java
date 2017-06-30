package com.github.api.gateway.filters.route.ribbon.util;

/**
 * Created by yifanzhang.
 */
public class RibbonClientSpecification implements NamedContextFactory.Specification {

    private String name;

    private Class<?>[] configuration;

    public RibbonClientSpecification(String name, Class<?>[] configuration) {
        this.name = name;
        this.configuration = configuration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?>[] getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Class<?>[] configuration) {
        this.configuration = configuration;
    }
}
