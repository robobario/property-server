package service.environment.model;

import static com.google.common.collect.Sets.union;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.Set;

public class ChildPropertyContainer implements PropertyContainer{
    protected final String name;
    private PropertyContainer parent;
    private PropertyContainer properties = new InMemoryPropertyContainer();

    public ChildPropertyContainer(PropertyContainer parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    @Override
    public Map<String, String> getAllProperties() {
        Set<String> allKeys = getAllPropertyKeys();
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        for(String key: allKeys){
            builder.put(key, get(key));
        }
        return builder.build();
    }

    @Override
    public Set<String> getAllPropertyKeys() {
        return union(properties.getAllPropertyKeys(), parent.getAllPropertyKeys());
    }

    @Override
    public String get(String propertyKey){
        String prop = properties.get(propertyKey);
        return prop != null ? prop : parent.get(propertyKey);
    }


    @Override
    public void put(String propertyKey, String propertyValue) {
        properties.put(propertyKey,propertyValue);
    }


    @Override
    public void remove(String propertyKey) {
        properties.remove(propertyKey);
    }


    public Map<String, String> getLocalProperties(){
        return properties.getAllProperties();
    }

    public PropertyContainer getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }
}
