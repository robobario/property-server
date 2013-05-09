package web.application;

import static service.environment.model.PropertyReplacingContainer.decorate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.environment.EnvironmentService;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(Routes.PROPERTY_HANDLER)
public class PropertyHandler {

    @Resource(name = Context.ENV_NAME)
    EnvironmentService environmentService;


    @RequestMapping(method = RequestMethod.GET, value = Routes.PROPERTY_GET)
    public
    @ResponseBody
    String viewEnvironment(HttpServletResponse resp, @PathVariable String appName, @PathVariable String propertyKey) {
        addAcal(resp);
        return decorate(environmentService.getCurrentEnvironment().getApplication(appName)).get(propertyKey);
    }


    @RequestMapping(value = "/**",method = RequestMethod.OPTIONS)
    public void commonOptions(HttpServletResponse theHttpServletResponse) throws IOException {
        theHttpServletResponse.addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-requested-with");
        theHttpServletResponse.addHeader("Access-Control-Max-Age", "60"); // seconds to cache preflight request --> less OPTIONS traffic
        theHttpServletResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        addAcal(theHttpServletResponse);
    }


    private void addAcal(HttpServletResponse theHttpServletResponse) {
        theHttpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
    }

}
