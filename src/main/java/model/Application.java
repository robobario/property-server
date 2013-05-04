package model;

public class Application extends ChildPropertyContainer {

    private Application(String applicationName, Environment environment) {
        super(environment, applicationName);
    }

    public static Application create(String applicationName, Environment environment) {
        return new Application(applicationName,environment);
    }
}
