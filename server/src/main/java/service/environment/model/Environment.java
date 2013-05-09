package service.environment.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class Environment extends ChildPropertyContainer{
    private Map<String,Environment> subEnvironments = ImmutableMap.of();
    private Map<String, Application> applications = ImmutableMap.of();

    private Environment(String subContainerName, PropertyContainer parent){
        super(parent, subContainerName);
    }

    public static Environment createRootEnvironment(){
        return new Environment("root",new NullContainer());
    }

    public Environment createSubEnvironment(String subContainerName){
        Environment sub = new Environment(subContainerName,this);
        subEnvironments = ImmutableMap.<String,Environment>builder().putAll(subEnvironments).put(subContainerName, sub).build();
        return sub;
    }

    public Application createApplication(String applicationName){
        Application application = Application.create(applicationName, this);
        applications = ImmutableMap.<String,Application>builder().putAll(applications).put(applicationName, application).build();
        return application;
    }

    public Map<String, Application> getAllApplications(){
        ImmutableMap.Builder<String, Application> builder = ImmutableMap.builder();
        builder.putAll(applications);
        for(Environment env : subEnvironments.values()) {
            builder.putAll(env.getAllApplications());
        }
        return builder.build();
    }

    public Application getApplication(String applicationName){
        return getAllApplications().get(applicationName);
    }


    public Set<Environment> getSubEnvironments() {
        return ImmutableSet.copyOf(subEnvironments.values());
    }


    public Collection<Application> getLocalApplications() {
        return applications.values();
    }

    public Environment findEnvironment(String name) {
        if(name.equals(getName())){
            return this;
        }else{
            for(Environment environment: subEnvironments.values()){
                Environment environment1 = environment.findEnvironment(name);
                if(environment1 != null){
                    return environment1;
                }
            }
        }
        return null;
    }
}
