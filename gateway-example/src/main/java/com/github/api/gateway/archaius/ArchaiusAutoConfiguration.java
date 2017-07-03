package com.github.api.gateway.archaius;

import com.netflix.config.*;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PreDestroy;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.netflix.config.ConfigurationManager.APPLICATION_PROPERTIES;
import static com.netflix.config.ConfigurationManager.DISABLE_DEFAULT_ENV_CONFIG;
import static com.netflix.config.ConfigurationManager.DISABLE_DEFAULT_SYS_CONFIG;
import static com.netflix.config.ConfigurationManager.ENV_CONFIG_NAME;
import static com.netflix.config.ConfigurationManager.SYS_CONFIG_NAME;
import static com.netflix.config.ConfigurationManager.URL_CONFIG_NAME;

/**
 * Bind the archaius to the spring context including the dynamic changes refresh.
 *
 * Created by yifanzhang.
 */
@Configuration
public class ArchaiusAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ArchaiusAutoConfiguration.class);

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    @Autowired
    private ConfigurableEnvironment env;

    @Autowired
    private List<AbstractConfiguration> externalConfigurations;

    @PreDestroy
    public void close() {
        setStatic(ConfigurationManager.class, "instance", null);
        setStatic(ConfigurationManager.class, "customConfigurationInstalled", false);
        setStatic(DynamicPropertyFactory.class, "config", null);
        setStatic(DynamicPropertyFactory.class, "initializedWithDefaultConfig", false);
        setStatic(DynamicProperty.class, "dynamicPropertySupportImpl", null);
        initialized.compareAndSet(true, false);
    }

    @Bean
    public ConfigurableEnvironmentConfiguration configurableEnvironmentConfiguration() {
        ConfigurableEnvironmentConfiguration envConfig = new ConfigurableEnvironmentConfiguration(
            this.env);
        configureArchaius(envConfig);
        return envConfig;
    }

//    @Configuration
//    @ConditionalOnClass(Endpoint.class)
//    protected static class ArchaiusEndpointConfiguration {
//        @Bean
//        protected ArchaiusEndpoint archaiusEndpoint() {
//            return new ArchaiusEndpoint();
//        }
//    }

    @Configuration
    protected static class PropagateEventsConfiguration
        implements ApplicationListener<EnvironmentChangeEvent> {
        @Autowired
        private Environment env;

        @Override
        public void onApplicationEvent(EnvironmentChangeEvent event) {
            AbstractConfiguration manager = ConfigurationManager.getConfigInstance();
            for (String key : event.getKeys()) {
                for (ConfigurationListener listener : manager
                    .getConfigurationListeners()) {
                    Object source = event.getSource();
                    // TODO: Handle add vs set vs delete?
                    int type = AbstractConfiguration.EVENT_SET_PROPERTY;
                    String value = this.env.getProperty(key);
                    boolean beforeUpdate = false;
                    listener.configurationChanged(new ConfigurationEvent(source, type,
                        key, value, beforeUpdate));
                }
            }
        }
    }

    protected void configureArchaius(ConfigurableEnvironmentConfiguration envConfig) {
        if (initialized.compareAndSet(false, true)) {
            String appName = this.env.getProperty("spring.application.name");
            if (appName == null) {
                appName = "application";
                log.warn("No spring.application.name found, defaulting to 'application'");
            }
            System.setProperty(DeploymentContext.ContextKey.appId.getKey(), appName);

            ConcurrentCompositeConfiguration config = new ConcurrentCompositeConfiguration();

            // support to add other Configurations (Jdbc, DynamoDb, Zookeeper, jclouds,
            // etc...)
            if (this.externalConfigurations != null) {
                for (AbstractConfiguration externalConfig : this.externalConfigurations) {
                    config.addConfiguration(externalConfig);
                }
            }
            config.addConfiguration(envConfig,
                ConfigurableEnvironmentConfiguration.class.getSimpleName());

            // below come from ConfigurationManager.createDefaultConfigInstance()
            DynamicURLConfiguration defaultURLConfig = new DynamicURLConfiguration();
            try {
                config.addConfiguration(defaultURLConfig, URL_CONFIG_NAME);
            }
            catch (Throwable ex) {
                log.error("Cannot create config from " + defaultURLConfig, ex);
            }

            // TODO: sys/env above urls?
            if (!Boolean.getBoolean(DISABLE_DEFAULT_SYS_CONFIG)) {
                SystemConfiguration sysConfig = new SystemConfiguration();
                config.addConfiguration(sysConfig, SYS_CONFIG_NAME);
            }
            if (!Boolean.getBoolean(DISABLE_DEFAULT_ENV_CONFIG)) {
                EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration();
                config.addConfiguration(environmentConfiguration, ENV_CONFIG_NAME);
            }

            ConcurrentCompositeConfiguration appOverrideConfig = new ConcurrentCompositeConfiguration();
            config.addConfiguration(appOverrideConfig, APPLICATION_PROPERTIES);
            config.setContainerConfigurationIndex(
                config.getIndexOfConfiguration(appOverrideConfig));

            addArchaiusConfiguration(config);
        }
        else {
            // TODO: reinstall ConfigurationManager
            log.warn(
                "Netflix ConfigurationManager has already been installed, unable to re-install");
        }
    }

    private void addArchaiusConfiguration(ConcurrentCompositeConfiguration config) {
        if (ConfigurationManager.isConfigurationInstalled()) {
            AbstractConfiguration installedConfiguration = ConfigurationManager
                .getConfigInstance();
            if (installedConfiguration instanceof ConcurrentCompositeConfiguration) {
                ConcurrentCompositeConfiguration configInstance = (ConcurrentCompositeConfiguration) installedConfiguration;
                configInstance.addConfiguration(config);
            }
            else {
                installedConfiguration.append(config);
                if (!(installedConfiguration instanceof AggregatedConfiguration)) {
                    log.warn(
                        "Appending a configuration to an existing non-aggregated installed configuration will have no effect");
                }
            }
        }
        else {
            ConfigurationManager.install(config);
        }
    }

    private static void setStatic(Class<?> type, String name, Object value) {
        // Hack a private static field
        Field field = ReflectionUtils.findField(type, name);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, null, value);
    }

}
