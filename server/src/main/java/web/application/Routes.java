package web.application;

public class Routes {

    public static final String ENV_NAME = "envName";

    public static final String APP_NAME = "appName";

    public static final String SUB_ENV_NAME = "subName";

    public static final String PROPERTY_KEY = "propertyKey";

    public static final String PROPERTY_VALUE = "propertyValue";

    private static final String ENV_NAME_TOKEN = "{" + ENV_NAME + "}";

    private static final String SUB_ENV_NAME_TOKEN = "{" + SUB_ENV_NAME + "}";

    private static final String APP_NAME_TOKEN = "{" + APP_NAME + "}";

    private static final String PROPERTY_KEY_TOKEN = "{" + PROPERTY_KEY + "}";

    private static final String PROPERTY_VALUE_TOKEN = "{" + PROPERTY_VALUE + "}";

    public static final String ENVIRONMENT_HANDLER = "/environment";

    public static final String APPLICATION_HANDLER = "/application";

    public static final String PROPERTY_HANDLER = "/properties";

    public static final String ENV_VIEW = ENV_NAME_TOKEN;

    public static final String ENV_CREATE_SUBENV = ENV_NAME_TOKEN + "/newSubContainer/" + SUB_ENV_NAME_TOKEN;

    public static final String ENV_CREATE_APP = ENV_NAME_TOKEN + "/newApplication/" + APP_NAME_TOKEN;

    public static final String PROPERTY_GET = APP_NAME_TOKEN + "/" + PROPERTY_KEY_TOKEN;

    public static final String ENV_PUT_PROPERTY = ENV_NAME_TOKEN;

    public static final String APP_VIEW = APP_NAME_TOKEN;

    public static final String APP_DELETE_PROPERTY = APP_NAME_TOKEN + "/" + PROPERTY_KEY_TOKEN;

    public static final String ENV_DELETE_PROPERTY = ENV_NAME_TOKEN + "/" + PROPERTY_KEY_TOKEN;

    public static final String APP_PUT_PROPERTY = APP_NAME_TOKEN;


    public static RouteBuilder to() {
        return new RouteBuilder();
    }


    public static class RouteBuilder {

        public EnvRouteBuilder environment() {
            return new EnvRouteBuilder();
        }


        public AppRouteBuilder application() {
            return new AppRouteBuilder();
        }
    }

    public static class EnvRouteBuilder {

        public String environmentDetails(String envName) {
            return ENVIRONMENT_HANDLER + "/" + ENV_VIEW.replace(ENV_NAME_TOKEN, envName);
        }


        public String addSubEnvironmentTo(String envName, String newSubEnvName) {
            return ENVIRONMENT_HANDLER + "/" + ENV_CREATE_SUBENV.replace(ENV_NAME_TOKEN, envName)
                    .replace(SUB_ENV_NAME_TOKEN, newSubEnvName);
        }


        public String root() {
            return ENVIRONMENT_HANDLER;
        }


        public String addApplicationTo(String environment, String appName) {
            return ENVIRONMENT_HANDLER + "/" + ENV_CREATE_APP.replace(ENV_NAME_TOKEN, environment)
                    .replace(APP_NAME_TOKEN, appName);
        }


        public String addPropertyTo(String envName) {
            return ENVIRONMENT_HANDLER + "/" + ENV_PUT_PROPERTY.replace(ENV_NAME_TOKEN, envName);
        }


        public String removePropertyFrom(String envName, String propertyKey) {
            return ENVIRONMENT_HANDLER + "/" + ENV_DELETE_PROPERTY.replace(ENV_NAME_TOKEN, envName)
                    .replace(PROPERTY_KEY_TOKEN, propertyKey);
        }
    }

    public static class AppRouteBuilder {

        public String applicationDetails(String appName) {
            return APPLICATION_HANDLER + "/" + APP_VIEW.replace(APP_NAME_TOKEN, appName);
        }


        public String addProperty(String applicationName) {
            return APPLICATION_HANDLER + "/" + APP_PUT_PROPERTY.replace(APP_NAME_TOKEN, applicationName);
        }


        public String removeProperty(String applicationName, String propertyKey) {
            return APPLICATION_HANDLER + "/" + APP_DELETE_PROPERTY.replace(APP_NAME_TOKEN, applicationName)
                    .replace(PROPERTY_KEY_TOKEN, propertyKey);
        }
    }
}
