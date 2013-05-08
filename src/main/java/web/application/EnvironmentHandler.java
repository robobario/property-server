package web.application;

import static web.view.ViewCreator.createEnvironmentView;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.environment.EnvironmentService;
import service.environment.model.Environment;
import web.view.EnvironmentView;

import javax.annotation.Resource;

@Controller
@RequestMapping(Routes.ENVIRONMENT_HANDLER)
public class EnvironmentHandler {

    @Resource(name = Context.ENV_NAME)
    EnvironmentService service;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody EnvironmentView viewRoot() {
        return createEnvironmentView(service.getCurrentEnvironment());
    }

    @RequestMapping(method = RequestMethod.GET, value = Routes.ENV_VIEW)
    public @ResponseBody EnvironmentView viewEnvironment(@PathVariable(Routes.ENV_NAME) String name) {
        return createEnvironmentView(service.getCurrentEnvironment().findEnvironment(name));
    }

    @RequestMapping(method = RequestMethod.PUT, value = Routes.ENV_CREATE_SUBENV)
    public @ResponseBody EnvironmentView addSubContainer(@PathVariable(Routes.ENV_NAME) String name,@PathVariable(Routes.SUB_ENV_NAME) String subName) {
        Environment env = service.getCurrentEnvironment().findEnvironment(name);
        env.createSubEnvironment(subName);
        service.update(env);
        return createEnvironmentView(env);
    }

    @RequestMapping(method = RequestMethod.PUT, value = Routes.ENV_CREATE_APP)
    public @ResponseBody EnvironmentView addApplication(@PathVariable(Routes.ENV_NAME) String name,@PathVariable(Routes.APP_NAME) String appName) {
        Environment env = service.getCurrentEnvironment().findEnvironment(name);
        env.createApplication(appName);
        service.update(env);
        return createEnvironmentView(env);
    }

    @RequestMapping(method = RequestMethod.PUT, value = Routes.ENV_PUT_PROPERTY)
    public @ResponseBody EnvironmentView addProperty(@PathVariable(Routes.ENV_NAME) String name,@PathVariable(Routes.PROPERTY_KEY) String propertyKey,@PathVariable(Routes.PROPERTY_VALUE) String propertyValue ) {
        Environment env = service.getCurrentEnvironment().findEnvironment(name);
        env.put(propertyKey, propertyValue);
        service.update(env);
        return createEnvironmentView(env);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = Routes.ENV_DELETE_PROPERTY)
    public @ResponseBody EnvironmentView remove(@PathVariable(Routes.ENV_NAME) String name,@PathVariable(Routes.PROPERTY_KEY) String propertyKey) {
        Environment env = service.getCurrentEnvironment().findEnvironment(name);
        env.remove(propertyKey);
        service.update(env);
        return createEnvironmentView(env);
    }


}
