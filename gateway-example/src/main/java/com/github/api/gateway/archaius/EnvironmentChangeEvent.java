package com.github.api.gateway.archaius;

import org.springframework.context.ApplicationEvent;

import java.util.Set;

/**
 * Created by yifanzhang.
 */
public class EnvironmentChangeEvent extends ApplicationEvent {

    private Set<String> keys;

    public EnvironmentChangeEvent(Set<String> keys) {
        super(keys);
        this.keys = keys;
    }

    /**
     * @return the keys
     */
    public Set<String> getKeys() {
        return keys;
    }

}
