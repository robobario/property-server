package web.application;

import static web.view.ViewCreator.createApplicationView;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.environment.EnvironmentService;
import service.environment.model.Application;
import service.environment.model.Environment;
import web.view.ApplicationView;
import web.view.ViewCreator;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(Routes.APPLICATION_HANDLER)
public class ApplicationHandler {

    @Resource(name = Context.ENV_NAME)
    EnvironmentService service;

    @RequestMapping(method = RequestMethod.GET, value = Routes.APP_VIEW)
    public @ResponseBody ApplicationView viewApplication(HttpServletResponse resp,@PathVariable(Routes.APP_NAME) String name) {
        addAcal(resp);
        return createApplicationView(service.getCurrentEnvironment().getApplication(name));
    }


    @RequestMapping(method = RequestMethod.POST, value = Routes.APP_PUT_PROPERTY)
    public @ResponseBody
    ApplicationView addProperty(HttpServletResponse resp,@PathVariable(Routes.APP_NAME) String name, @RequestBody AddPropRequest request) {
        addAcal(resp);
        Environment currentEnvironment = service.getCurrentEnvironment();
        Application app = currentEnvironment.getApplication(name);
        app.put(request.getKey(),request.getValue());
        service.update(currentEnvironment);
        return ViewCreator.createApplicationView(app);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = Routes.APP_DELETE_PROPERTY)
    public @ResponseBody
    ApplicationView removeProperty(HttpServletResponse resp,@PathVariable(Routes.APP_NAME) String name,@PathVariable(Routes.PROPERTY_KEY) String propertyKey) {
        addAcal(resp);
        Environment currentEnvironment = service.getCurrentEnvironment();
        Application app = currentEnvironment.getApplication(name);
        app.remove(propertyKey);
        service.update(currentEnvironment);
        return ViewCreator.createApplicationView(app);
    }

    @RequestMapping(value = "/**",method = RequestMethod.OPTIONS)
    public void commonOptions(HttpServletResponse theHttpServletResponse) throws IOException {
        theHttpServletResponse.addHeader("Access-Control-Allow-Headers",
                "origin, content-type, accept, x-requested-with");
        theHttpServletResponse.addHeader("Access-Control-Max-Age", "60"); // seconds to cache preflight request --> less OPTIONS traffic
        theHttpServletResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        addAcal(theHttpServletResponse);
    }

    private void addAcal(HttpServletResponse resp) {
        resp.addHeader("Access-Control-Allow-Origin", "*");
    }


}
