package model;

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
        Application application = getAllApplications().get(applicationName);
        if(application == null){
            application = createApplication(applicationName);
        }
        return application;
    }


    public Set<Environment> getSubEnvironments() {
        return ImmutableSet.copyOf(subEnvironments.values());
    }


    public Collection<Application> getLocalApplications() {
        return applications.values();
    }
}
