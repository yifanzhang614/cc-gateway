package com.github.api.gateway.archaius;

import org.apache.commons.configuration.AbstractConfiguration;
import org.springframework.core.env.*;

import java.util.*;

/**
 * Created by yifanzhang.
 */
public class ConfigurableEnvironmentConfiguration extends AbstractConfiguration {

    private final ConfigurableEnvironment environment;

    public ConfigurableEnvironmentConfiguration(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override
    protected void addPropertyDirect(String key, Object value) {

    }

    @Override
    public boolean isEmpty() {
        return !getKeys().hasNext(); // TODO: find a better way to do this
    }

    @Override
    public boolean containsKey(String key) {
        return this.environment.containsProperty(key);
    }

    @Override
    public Object getProperty(String key) {
        return this.environment.getProperty(key);
    }

    @Override
    public Iterator<String> getKeys() {
        List<String> result = new ArrayList<String>();
        for (Map.Entry<String, PropertySource<?>> entry : getPropertySources().entrySet()) {
            PropertySource<?> source = entry.getValue();
            if (source instanceof EnumerablePropertySource) {
                EnumerablePropertySource<?> enumerable = (EnumerablePropertySource<?>) source;
                for (String name : enumerable.getPropertyNames()) {
                    result.add(name);
                }
            }
        }
        return result.iterator();
    }

    private Map<String, PropertySource<?>> getPropertySources() {
        Map<String, PropertySource<?>> map = new LinkedHashMap<String, PropertySource<?>>();
        MutablePropertySources sources = (this.environment != null ? this.environment
            .getPropertySources() : new StandardEnvironment().getPropertySources());
        for (PropertySource<?> source : sources) {
            extract("", map, source);
        }
        return map;
    }

    private void extract(String root, Map<String, PropertySource<?>> map,
        PropertySource<?> source) {
        if (source instanceof CompositePropertySource) {
            for (PropertySource<?> nest : ((CompositePropertySource) source)
                .getPropertySources()) {
                extract(source.getName() + ":", map, nest);
            }
        }
        else {
            map.put(root + source.getName(), source);
        }
    }

}
