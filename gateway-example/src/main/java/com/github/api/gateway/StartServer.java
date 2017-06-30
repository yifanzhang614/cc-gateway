/*
 * Copyright 2013 Netflix, Inc.
 *
 *      Licensed under the Apache License, Version 2.0 (the "License");
 *      you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *      Unless required by applicable law or agreed to in writing, software
 *      distributed under the License is distributed on an "AS IS" BASIS,
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *      limitations under the License.
 */

package com.github.api.gateway;

import com.github.api.gateway.filters.RouteLocator;
import com.github.api.gateway.filters.SimpleRouteLocator;
import com.github.api.gateway.filters.ZuulProperties;
import com.github.api.gateway.filters.post.SendErrorFilter;
import com.github.api.gateway.filters.pre.PreDecorationFilter;
import com.github.api.gateway.filters.route.ProxyRequestHelper;
import com.github.api.gateway.filters.route.SendForwardFilter;
import com.github.api.gateway.filters.route.ribbon.client.CustomRestRibbonCommandFactory;
import com.github.api.gateway.filters.route.ribbon.util.SpringClientFactory;
import com.github.api.gateway.filters.route.ribbon.RibbonCommandFactory;
import com.github.api.gateway.filters.route.ribbon.RibbonRoutingFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gateway.util.Utils;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.zuul.FilterFileManager;
import com.netflix.zuul.FilterLoader;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.filters.FilterRegistry;
import com.netflix.zuul.groovy.GroovyCompiler;
import com.netflix.zuul.groovy.GroovyFileFilter;
import com.netflix.zuul.monitoring.MonitoringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

public class StartServer implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(StartServer.class);
    private static final String ZUUL_CONFIG_NAMESPACE = "zuul";
    private static final String SERVICE_CONFIG_NAMESPACE = "services";

    private DynamicStringProperty  zuulConfigFileName = DynamicPropertyFactory
        .getInstance().getStringProperty("app.config.file","app.yml");

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("starting server");

        // mocks monitoring infrastructure as we don't need it for this simple app
        MonitoringHelper.initMocks();

        // initializes groovy filesystem poller
        initGroovyFilterManager();

        // initializes a few java filter examples
        initJavaFilters();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("stopping server");
        FilterRegistry registry = FilterRegistry.instance();
        Collection<ZuulFilter> filters = registry.getAllFilters();
        filters.clear();
        clearLoaderCache();
    }

    private void clearLoaderCache() {
        FilterLoader instance = FilterLoader.getInstance();
        Field field = ReflectionUtils.findField(FilterLoader.class, "hashFiltersByType");
        ReflectionUtils.makeAccessible(field);
        @SuppressWarnings("rawtypes")
        Map cache = (Map) ReflectionUtils.getField(field, instance);
        cache.clear();
    }

    private void initGroovyFilterManager() {
        FilterLoader.getInstance().setCompiler(new GroovyCompiler());

        String scriptRoot = System.getProperty("zuul.filter.root", "");
        if (scriptRoot.length() > 0) scriptRoot = scriptRoot + File.separator;
        try {
            FilterFileManager.setFilenameFilter(new GroovyFileFilter());
            FilterFileManager.init(5, scriptRoot + "pre", scriptRoot + "route", scriptRoot + "post", scriptRoot + "error");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initJavaFilters()  {
        final FilterRegistry r = FilterRegistry.instance();

        Map<String,Object> yamlObj = Utils.transferYaml2Prop(null, zuulConfigFileName.get());
        if (yamlObj == null) {
            throw new RuntimeException("Can't find the configuration file:"+zuulConfigFileName.get());
        }

        ObjectMapper mapper=new ObjectMapper();
        try {
            // Map the zuul properties
            String zuulPropsJason  = mapper.writeValueAsString(yamlObj.get(ZUUL_CONFIG_NAMESPACE));
            ZuulProperties zuulProperties = mapper.readValue(zuulPropsJason, ZuulProperties.class);
            String prettyPrint = mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(zuulProperties);
            log.info("Got zuul properties: "+prettyPrint);
            zuulProperties.init();

            // Map the services configured for Ribbon
            Map<String,Object> serviceIdsMap = (Map<String,Object>)yamlObj.get(SERVICE_CONFIG_NAMESPACE);
            Map<String,Object> flatServiceIdsMap = Utils.getFlattenedMap(serviceIdsMap);
            Properties serviceIdsProps = new Properties();
            serviceIdsProps.putAll(flatServiceIdsMap);
            ConfigurationManager.loadProperties(serviceIdsProps);

            RouteLocator routeLocator = new SimpleRouteLocator(null,zuulProperties);
            routeLocator.getRoutes(); // force refresh routes
            r.put("preDecorationFilter",new PreDecorationFilter(routeLocator,zuulProperties));

            RibbonCommandFactory rcf = new CustomRestRibbonCommandFactory(new SpringClientFactory());
            ProxyRequestHelper helper = new ProxyRequestHelper();
            //        helper.setIgnoredHeaders(zuulProperties.getIgnoredHeaders());
            RibbonRoutingFilter rrFilter = new RibbonRoutingFilter(helper,rcf);
            r.put("ribbonRoutingFilter",rrFilter);

            r.put("forwardFilter", new SendForwardFilter());
            r.put("sendErrorFilter", new SendErrorFilter());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Construct properties from yaml file error!",e);
        } catch (IOException e1){
            throw new RuntimeException("Construct properties from yaml file error!",e1);
        }

    }

}
