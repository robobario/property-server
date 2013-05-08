package web.application;

import static web.view.ViewCreator.createApplicationView;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.environment.EnvironmentService;
import service.environment.model.Application;
import service.environment.model.Environment;
import web.view.ApplicationView;
import web.view.ViewCreator;

import javax.annotation.Resource;

@Controller
@RequestMapping(Routes.APPLICATION_HANDLER)
public class ApplicationHandler {

    @Resource(name = Context.ENV_NAME)
    EnvironmentService service;

    @RequestMapping(method = RequestMethod.GET, value = Routes.APP_VIEW)
    public @ResponseBody ApplicationView viewApplication(@PathVariable(Routes.APP_NAME) String name) {
        return createApplicationView(service.getCurrentEnvironment().getApplication(name));
    }

    @RequestMapping(method = RequestMethod.PUT, value = Routes.APP_PUT_PROPERTY)
    public @ResponseBody
    ApplicationView addProperty(@PathVariable(Routes.APP_NAME) String name,@PathVariable(Routes.PROPERTY_KEY) String propertyKey,@PathVariable(Routes.PROPERTY_VALUE) String propertyValue ) {
        Environment currentEnvironment = service.getCurrentEnvironment();
        Application app = currentEnvironment.getApplication(name);
        app.put(propertyKey,propertyValue);
        service.update(currentEnvironment);
        return ViewCreator.createApplicationView(app);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = Routes.APP_DELETE_PROPERTY)
    public @ResponseBody
    ApplicationView removeProperty(@PathVariable(Routes.APP_NAME) String name,@PathVariable(Routes.PROPERTY_KEY) String propertyKey) {
        Environment currentEnvironment = service.getCurrentEnvironment();
        Application app = currentEnvironment.getApplication(name);
        app.remove(propertyKey);
        service.update(currentEnvironment);
        return ViewCreator.createApplicationView(app);
    }



}
