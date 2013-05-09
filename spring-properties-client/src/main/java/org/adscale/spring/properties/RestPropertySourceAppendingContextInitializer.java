package org.adscale.spring.properties;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class RestPropertySourceAppendingContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final String PROPERTY_SERVER_URL_KEY = "property.server.url";

    public static final String APPLICATION_NAME_KEY = "application.name";

    private static Logger log = LoggerFactory.getLogger(RestPropertySourceAppendingContextInitializer.class);


    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        log.info("registering REST property source");
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String url = environment.getProperty(PROPERTY_SERVER_URL_KEY);
        String name = environment.getProperty(APPLICATION_NAME_KEY);
        if (!Strings.isNullOrEmpty(url) && !Strings.isNullOrEmpty(name)) {
            RestPropertyClient client = new RestPropertyClient(url, name);
            RestPropertySource propertySource = new RestPropertySource("rest-property-source", client);
            environment.getPropertySources().addLast(propertySource);
            log.info("registered REST property source with " + PROPERTY_SERVER_URL_KEY + " " + url + " and app name "
                    + name);
        }
        else {
            log.warn("did not register REST property source please supply env variables or system props "
                    + PROPERTY_SERVER_URL_KEY + " and " + APPLICATION_NAME_KEY);
        }
    }
}
