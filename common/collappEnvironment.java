
package io.collapp.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

public class collappEnvironment {

    private final ConfigurableEnvironment environment;

    private static final String collapp_CONFIG_LOCATION = "collapp.config.location";

    private static final Logger LOG = LogManager.getLogger();

    public collappEnvironment(ConfigurableEnvironment environment) {
        this.environment = environment;

        if (environment.containsProperty(collapp_CONFIG_LOCATION) && StringUtils.isNotBlank(environment.getProperty(collapp_CONFIG_LOCATION))) {

            String configLocation = environment.getProperty(collapp_CONFIG_LOCATION);

            LOG.info("Detected config file {}, loading it", configLocation);
            try {
                environment.getPropertySources().addFirst(new ResourcePropertySource(new UrlResource(configLocation)));
            } catch (IOException ioe) {
                throw new IllegalStateException("error while loading external configuration file at " + configLocation, ioe);
            }
        }

        setSystemPropertyIfNull(environment, "datasource.dialect", "HSQLDB");
        setSystemPropertyIfNull(environment, "datasource.url", "jdbc:hsqldb:mem:collapp");
        setSystemPropertyIfNull(environment, "datasource.username", "sa");
        setSystemPropertyIfNull(environment, "datasource.password", "");
        setSystemPropertyIfNull(environment, "spring.profiles.active", "dev");

        logUse("datasource.dialect");
        logUse("datasource.url");
        logUse("datasource.username");
        logUse("spring.profiles.active");

    }

    private void logUse(String name) {
        LOG.info("For property {}, the value is: {}", name, environment.getProperty(name));
    }


    private static void setSystemPropertyIfNull(ConfigurableEnvironment env, String name, String value) {
        if(!env.containsProperty(name) || StringUtils.isBlank(env.getProperty(name))) {
            LOG.warn("Property {} is not set, using default value: {}", name, value);
            Map<String, Object> source = Collections.singletonMap(name, (Object) value);
            env.getPropertySources().addFirst(new MapPropertySource(name, source));
        }
    }

    public String getProperty(String key) {
        return environment.getProperty(key);
    }

    public boolean containsProperty(String key) {
        return environment.containsProperty(key);
    }

    public String getRequiredProperty(String key) {
        return environment.getRequiredProperty(key);
    }

    public String[] getActiveProfiles() {
        return environment.getActiveProfiles();
    }
}
