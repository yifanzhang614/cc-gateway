package com.github.api.gateway.support.property;

import com.github.api.gateway.util.Utils;

import java.util.Map;
import java.util.Properties;

/**
 * Created by chdyan on 16/8/2.
 */
public class AppIdSecretProperties {
    private Properties properties = new Properties();

    private AppIdSecretProperties() {
        init();
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }

    private void init(){
        String ymlPath = "auth/appIdSecret.yml";
        Map<String,Object> yamlObj = Utils.transferYaml2Prop(null, ymlPath);
        if (yamlObj == null) {
            throw new RuntimeException("Can't find the configuration file:" + ymlPath);
        }
        yamlObj = (Map<String, Object>) yamlObj.get("app");
        yamlObj = Utils.getFlattenedMap(yamlObj);
        properties.putAll(yamlObj);
    }

    private static AppIdSecretProperties instance = new AppIdSecretProperties();

    public static String get(String key) {
        if(key == null) {
            return null;
        }
        return instance.getValue(key);
    }
    public static String getCustomer(String key) {
        if(key == null) {
            return null;
        }
        return get(key + ".customer");
    }
    public static String getSecret(String key) {
        if(key == null) {
            return null;
        }
        return get(key + ".secret");
    }
}
