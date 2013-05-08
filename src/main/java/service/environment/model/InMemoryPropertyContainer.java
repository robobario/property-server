package service.environment.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InMemoryPropertyContainer implements PropertyContainer{

    private ImmutableMap<String, String> properties = ImmutableMap.of();

    @Override
    public Map<String, String> getAllProperties() {
        return properties;
    }

    @Override
    public Set<String> getAllPropertyKeys() {
        return properties.keySet();
    }

    @Override
    public String get(String propertyKey) {
        return properties.get(propertyKey);
    }

    @Override
    public void put(String propertyKey, String propertyValue) {
        properties = ImmutableMap.<String, String>builder().putAll(properties).put(propertyKey,propertyValue).build();
    }


    @Override
    public void remove(String propertyKey) {
        HashMap<String,String> temp = Maps.newHashMap();
        temp.putAll(properties);
        temp.remove(propertyKey);
        properties = ImmutableMap.copyOf(temp);
    }
}
