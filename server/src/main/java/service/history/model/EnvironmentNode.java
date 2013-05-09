package service.history.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import service.environment.model.Application;
import service.environment.model.Environment;

import java.util.Map;
import java.util.Set;

public class EnvironmentNode extends PropertyNode {
    private final Set<EnvironmentNode> subEnvironments;
    private final Set<PropertyNode> applicationNodes;

    @JsonCreator
    public EnvironmentNode(@JsonProperty("subEnvironments")Set<EnvironmentNode> subEnvironments,
                           @JsonProperty("applicationNodes")Set<PropertyNode> applicationNodes,
                           @JsonProperty("name")String name,
                           @JsonProperty("properties")Map<String, String> properties) {
        super(name, properties);
        this.subEnvironments = subEnvironments;
        this.applicationNodes = applicationNodes;
    }

    public Set<EnvironmentNode> getSubEnvironments() {
        return subEnvironments;
    }

    public Set<PropertyNode> getApplicationNodes() {
        return applicationNodes;
    }

    public static EnvironmentNode nodeOf(Environment environment) {
        Set<EnvironmentNode> subNodes = Sets.newHashSet();
        Set<PropertyNode> apps = Sets.newHashSet();

        for (Environment env : environment.getSubEnvironments()) {
            subNodes.add(nodeOf(env));
        }

        for (Application application : environment.getLocalApplications()) {
            apps.add(PropertyNode.nodeOf(application));
        }

        return new EnvironmentNode(ImmutableSet.copyOf(subNodes), ImmutableSet.copyOf(apps), environment.getName(), ImmutableMap.copyOf(environment.getLocalProperties()));
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName(), getProperties(), super.hashCode());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof EnvironmentNode) {
            final EnvironmentNode other = (EnvironmentNode) obj;
            return Objects.equal(getApplicationNodes(), other.getApplicationNodes())
                    && Objects.equal(getSubEnvironments(), other.getSubEnvironments()) && super.equals(obj);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("subEnvironments", subEnvironments).add("apps", applicationNodes).add("name", getName()).add("props", getProperties()).toString();
    }

    public static Environment copyTo(EnvironmentNode source, Environment target) {
        for (PropertyNode propertyNode : source.getApplicationNodes()) {
            Application application = target.createApplication(propertyNode.getName());
            for (Map.Entry<String, String> propertyEntry : propertyNode.getProperties().entrySet()) {
                application.put(propertyEntry.getKey(), propertyEntry.getValue());
            }
        }
        for (EnvironmentNode envNode : source.getSubEnvironments()) {
            Environment subEnvironment = target.createSubEnvironment(envNode.getName());
            copyTo(envNode, subEnvironment);
        }
        for (Map.Entry<String, String> propertyEntry : source.getProperties().entrySet()) {
            target.put(propertyEntry.getKey(), propertyEntry.getValue());
        }
        return target;
    }
}
