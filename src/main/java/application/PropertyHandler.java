package application;

import model.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

import static model.PropertyReplacingContainer.decorate;

@Controller
@RequestMapping(Routes.PROPERTY_HANDLER)
public class PropertyHandler {

    @Resource(name = Context.ENV_NAME)
    Environment environment;

    @RequestMapping(method = RequestMethod.GET, value = Routes.PROPERTY_GET)
    public @ResponseBody String viewEnvironment(@PathVariable String appName,@PathVariable String propertyKey) {
        return decorate(environment.getApplication(appName)).get(propertyKey);
    }
}
