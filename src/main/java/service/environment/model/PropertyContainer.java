package service.environment.model;

import java.util.Map;
import java.util.Set;

public interface PropertyContainer {
    public Map<String,String> getAllProperties();
    public Set<String> getAllPropertyKeys();
    public String get(String propertyKey);
    public void put(String propertyKey,String propertyValue);
}
