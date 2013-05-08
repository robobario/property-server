package service.environment.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Map;
import java.util.Set;

public class NullContainer implements PropertyContainer{
    @Override
    public Map<String, String> getAllProperties() {
        return ImmutableMap.of();
    }

    @Override
    public Set<String> getAllPropertyKeys() {
        return ImmutableSet.of();
    }

    @Override
    public String get(String propertyKey) {
        return null;
    }

    @Override
    public void put(String propertyKey, String propertyValue) {
        throw new RuntimeException("attempted to put a property into the null container");
    }
}
