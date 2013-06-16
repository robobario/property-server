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

    public static final String AMAZON_PROPERTY_SERVER_URL_KEY = "PARAM1";

    public static final String APPLICATION_NAME_KEY = "application.name";

    public static final String AMAZON_APPLICATION_NAME_KEY = "PARAM2";

    private static Logger log = LoggerFactory.getLogger(RestPropertySourceAppendingContextInitializer.class);


    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        log.info("registering REST property source");
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String url = getPropertyServerUrl(environment);
        String name = getApplicationName(environment);
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


    private String getApplicationName(ConfigurableEnvironment environment) {
        return getPropertyWithAmazonFallback(environment, APPLICATION_NAME_KEY, AMAZON_APPLICATION_NAME_KEY);
    }


    private String getPropertyWithAmazonFallback(ConfigurableEnvironment environment, String key, String amazonKey) {
        String nicelyNamedProperty = environment.getProperty(key);
        String amazonProperty = getAmazonProp(environment, amazonKey);
        return nicelyNamedProperty != null ? nicelyNamedProperty : amazonProperty;
    }


    private String getAmazonProp(ConfigurableEnvironment environment, String amazonKey) {
        String property = environment.getProperty(amazonKey);
        if(property != null && property.startsWith("\"") && property.endsWith("\"") && property.length()>2){
            return property.substring(1, property.length() - 1);
        }
        return property;
    }


    private String getPropertyServerUrl(ConfigurableEnvironment environment) {
        return getPropertyWithAmazonFallback(environment, PROPERTY_SERVER_URL_KEY, AMAZON_PROPERTY_SERVER_URL_KEY);
    }
}
