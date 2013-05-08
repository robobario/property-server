package web.application;

public class Routes {
    public static final String ENVIRONMENT_HANDLER = "/environment";
    public static final String PROPERTY_HANDLER = "/properties";
    private static final String NAME_TOKEN = "{name}";
    public static final String ENV_VIEW = NAME_TOKEN;
    public static final String ENV_CREATE_SUBENV = "{name}/newSubContainer/{subName}";
    public static final String ENV_CREATE_APP = "{name}/newApplication/{subName}";
    public static final String PROPERTY_GET = "{appName}/{propertyKey}";

    public static RouteBuilder to(){
        return new RouteBuilder();
    }

    public static class RouteBuilder{
        public EnvRouteBuilder environment(){
            return new EnvRouteBuilder();
        }
    }

    public static class EnvRouteBuilder{
        public String environmentDetails(String envName){
            return ENVIRONMENT_HANDLER + "/" + ENV_VIEW.replace(NAME_TOKEN, envName);
        }
    }
}
