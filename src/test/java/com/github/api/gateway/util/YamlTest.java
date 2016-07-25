package com.github.api.gateway.util;

import com.github.api.gateway.filters.ZuulProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Method;
import java.util.Map;


/**
 * Created by yifanzhang.
 */
public class YamlTest {

    public static void transMap2Bean(Map<String, Object> map, Object obj) {

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();

                if (map.containsKey(key)) {
                    Object value = map.get(key);
                    // 得到property对应的setter方法
                    Method setter = property.getWriteMethod();
                    setter.invoke(obj, value);
                }

            }

        } catch (Exception e) {
            System.out.println("transMap2Bean Error " + e);
        }

        return;

    }

    //@Test
    public void testSpringPropertiesYaml() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("app.yml"));
//        yaml.setResources(new FileSystemResource("/Users/yifanzhang/test/cc-gateway/src/test/resources/app.yml"));
        propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());

        System.out.println(propertySourcesPlaceholderConfigurer);
    }

    //@Test
    public void testSpringMapYaml() throws IOException {
        Yaml yaml = new Yaml();
        InputStream ios = new FileInputStream(new File("/Users/yifanzhang/test/cc-gateway/src/test/resources/app.yml"));
        Map< String, Object> yamlObj = (Map< String, Object>) yaml.load(ios);

        System.out.println(yamlObj);
        ObjectMapper mapper=new ObjectMapper();
        String mapAsJson = mapper.writeValueAsString(yamlObj.get("zuul"));
        System.out.println("Json String:"+mapAsJson);

        ZuulProperties zuulProp = mapper.readValue(mapAsJson,ZuulProperties.class);
        String prettyPrint = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(zuulProp);
        System.out.println("Zuul Properties:"+prettyPrint);
    }

//    @Test
    public void testParseMeYaml() throws FileNotFoundException {

//        Representer representer = new Representer();
//        representer.getPropertyUtils().setSkipMissingProperties(true);
        Yaml yaml = new Yaml();
        InputStream ios = new FileInputStream(new File("/Users/yifanzhang/test/cc-gateway/src/test/resources/app.yml"));
        Map< String, Object> result = (Map< String, Object>) yaml.load(ios);
//        ZuulProperties me = yaml.loadAs(new FileInputStream(new File("/Users/yifanzhang/test/cc-gateway/src/test/resources/app.yml")), ZuulProperties.class);
        System.out.println(result);

        Map<String,Object> value = (Map<String,Object>)result.get("zuul");
        if (value != null) {
            ZuulProperties properties = new ZuulProperties();
            transMap2Bean(value,properties);
            System.out.println(properties);
        }

//        Field[] f = ZuulProperties.class.getDeclaredFields();
//        for(Field field : f){
//            field.setAccessible(true);
//
//            Map<String,Object> level0Val = (Map<String,Object>)result.get("zuul");
//            if (level0Val != null) {
//                String fieldName = field.getName();
//                Type type = field.getGenericType();
//                if (result.get(fieldName) != null) {
//
//                }
//            }
//        }

    }
}
