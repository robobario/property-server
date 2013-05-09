package org.adscale.spring.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class RestPropertySourceAppendingContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private static Logger log = LoggerFactory.getLogger(RestPropertySourceAppendingContextInitializer.class);


    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        log.info("registering rest property source appending context initializer");
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String url = environment.getProperty("property.server.url", "http://localhost:8888");
        String name = environment.getProperty("application.name", "app");
        RestPropertyClient client = new RestPropertyClient(url, name);
        RestPropertySource propertySource = new RestPropertySource("rest-property-source", client);
        environment.getPropertySources().addLast(propertySource);
        log.info("finished registering rest property source appending context initializer");
    }
}
