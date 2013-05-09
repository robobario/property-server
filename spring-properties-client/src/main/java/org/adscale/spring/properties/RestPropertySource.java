package org.adscale.spring.properties;

import org.springframework.core.env.PropertySource;

public class RestPropertySource extends PropertySource<PropertyClient> {

    public RestPropertySource(String name, PropertyClient client) {
        super(name, client);
    }


    @Override
    public Object getProperty(String name) {
        return getSource().getProperty(name);
    }
}
