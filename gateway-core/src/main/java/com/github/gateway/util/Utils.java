package com.github.gateway.util;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by yifanzhang.
 */
public class Utils {
    public static final Map< String, Object> transferYaml2Prop(String classPath,String fileName) {

        String propPath = "/";
        if (classPath != null && !classPath.equals("")) {
            propPath = classPath + fileName;
        } else {
            propPath += fileName;
        }

        InputStream ios = Thread.currentThread().getContextClassLoader().getResourceAsStream(propPath);
        if (ios != null) {
            Yaml yaml = new Yaml();
            Map< String, Object> yamlObj = (Map< String, Object>) yaml.load(ios);
            return yamlObj;
        }
        return null;
    }

    public static final Map<String, Object> getFlattenedMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        buildFlattenedMap(result, source, null);
        return result;
    }

    private static void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, String path) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.hasText(path)) {
                if (key.startsWith("[")) {
                    key = path + key;
                }
                else {
                    key = path + "." + key;
                }
            }
            Object value = entry.getValue();
            if (value instanceof String) {
                result.put(key, value);
            }
            else if (value instanceof Map) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) value;
                buildFlattenedMap(result, map, key);
            }
            else if (value instanceof Collection) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                Collection<Object> collection = (Collection<Object>) value;
                int count = 0;
                for (Object object : collection) {
                    buildFlattenedMap(result,
                        Collections.singletonMap("[" + (count++) + "]", object), key);
                }
            }
            else {
                result.put(key, value == null ? "" : value);
            }
        }
    }
}
