package com.github.gateway.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by yifanzhang.
 */
public class YamlMap extends YamlProcessor {
    private Map<String, Object> map;

    public YamlMap(Map<String, Object> map) {
        if (map != null) {
            this.map = map;
        } else {
            this.map = createMap();
        }
    }

    public YamlMap() {
        this.map = createMap();
    }

    public Map<String, Object> getMap() {
        return (this.map != null) ? map : createMap();
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    protected Map<String, Object> createMap() {
        final Map<String, Object> result = new LinkedHashMap<String, Object>();
        process(new MatchCallback() {
            @Override
            public void process(Properties properties, Map<String, Object> map) {
                merge(result, map);
            }
        });
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void merge(Map<String, Object> output, Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Object existing = output.get(key);
            if (value instanceof Map && existing instanceof Map) {
                Map<String, Object> result = new LinkedHashMap<String, Object>((Map) existing);
                merge(result, (Map) value);
                output.put(key, result);
            }
            else {
                output.put(key, value);
            }
        }
    }
}
