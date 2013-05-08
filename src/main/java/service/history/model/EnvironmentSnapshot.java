package service.history.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import service.environment.model.Environment;

public class EnvironmentSnapshot {
    private final EnvironmentNode root;

    @JsonCreator
    public EnvironmentSnapshot(@JsonProperty("root")EnvironmentNode root) {
        this.root = root;
    }

    public static EnvironmentSnapshot snapshotOf(Environment env){
        return new EnvironmentSnapshot(EnvironmentNode.nodeOf(env));
    }

    public static Environment environmentFor(EnvironmentSnapshot env) {
        return EnvironmentNode.copyTo(env.getRoot(), Environment.createRootEnvironment());
    }


    @Override
    public int hashCode(){
        return Objects.hashCode(root);
    }

    @Override
    public boolean equals(final Object obj){
        if(obj instanceof EnvironmentSnapshot){
            final EnvironmentSnapshot other = (EnvironmentSnapshot) obj;
            return Objects.equal(root, other.root);
        } else{
            return false;
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this.getClass()).add("root",root).toString();
    }

    public EnvironmentNode getRoot() {
        return root;
    }
}
