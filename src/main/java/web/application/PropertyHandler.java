package web.application;

import static service.environment.model.PropertyReplacingContainer.decorate;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.environment.EnvironmentService;

import javax.annotation.Resource;

@Controller
@RequestMapping(Routes.PROPERTY_HANDLER)
public class PropertyHandler {

    @Resource(name = Context.ENV_NAME)
    EnvironmentService environmentService;

    @RequestMapping(method = RequestMethod.GET, value = Routes.PROPERTY_GET)
    public @ResponseBody String viewEnvironment(@PathVariable String appName,@PathVariable String propertyKey) {
        return decorate(environmentService.getCurrentEnvironment().getApplication(appName)).get(propertyKey);
    }
}
