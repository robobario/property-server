package web.view;

import service.environment.model.*;

public class ViewCreator {
    public static EnvironmentView createEnvironmentView(Environment environment){
        EnvironmentView environmentView = EnvironmentView.create(environment.getName());
        addProperties(environment, environmentView);
        addApplications(environment, environmentView);
        addSubApplications(environment, environmentView);
        return environmentView;
    }

    private static void addSubApplications(Environment environment, EnvironmentView environmentView) {
        for(Environment sub : environment.getSubEnvironments()){
            environmentView.addSubEnvironment(createEnvironmentView(sub));
        }
    }

    private static void addApplications(Environment environment, EnvironmentView environmentView) {
        for(Application application: environment.getLocalApplications()){
            ApplicationView applicationView = ApplicationView.create(application.getName());
            addProperties(application, applicationView);
            environmentView.addApplication(applicationView);
        }
    }

    private static void addProperties(ChildPropertyContainer environment, PropertyContainerView view) {
        PropertyContainer deriver = PropertyReplacingContainer.decorate(environment);
        for (String key : environment.getAllPropertyKeys()) {
            if(environment.getLocalProperties().containsKey(key)){
                view.addLocalProp(key, environment.get(key), deriver.get(key));
            }else{
                view.addInheritedProp(key, environment.get(key), deriver.get(key));
            }
        }
    }

}
