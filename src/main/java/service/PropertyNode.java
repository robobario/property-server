package service;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import model.Application;

import java.util.Map;

public class PropertyNode {
    private final String name;
    private final Map<String, String> properties;

    public PropertyNode(String name, Map<String, String> properties) {
        this.name = name;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public static PropertyNode nodeOf(Application application) {
        return new PropertyNode(application.getName(), ImmutableMap.copyOf(application.getLocalProperties()));
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(getName(), getProperties());
    }

    @Override
    public boolean equals(final Object obj){
        if(obj instanceof PropertyNode){
            final PropertyNode other = (PropertyNode) obj;
            return Objects.equal(getName(), other.getName())
                    &&  Objects.equal(getProperties(), other.getProperties());
        } else{
            return false;
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("properties",properties).add("name",getName()).toString();
    }
}
