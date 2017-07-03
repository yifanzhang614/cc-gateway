package com.github.gateway.support.property;


import com.github.gateway.util.Utils;

import java.util.Map;
import java.util.Properties;

/**
 * Created by chdyan on 16/8/2.
 */
public class UserProperties {

    private Properties properties = new Properties();

    private UserProperties() {
        init();
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }

    private void init(){
        String ymlPath = "auth/user.yml";
        Map<String,Object> yamlObj = Utils.transferYaml2Prop(null, ymlPath);
        if (yamlObj == null) {
            throw new RuntimeException("Can't find the configuration file:" + ymlPath);
        }
        yamlObj = (Map<String, Object>) yamlObj.get("user");
        yamlObj = Utils.getFlattenedMap(yamlObj);
        properties.putAll(yamlObj);
    }

    private static UserProperties instance = new UserProperties();

    public static String get(String key) {
        if(key == null) {
            return null;
        }
        return instance.getValue(key);
    }


}
