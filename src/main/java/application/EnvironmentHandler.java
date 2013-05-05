package application;

import model.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import view.EnvironmentView;

import javax.annotation.Resource;

import static view.ViewCreator.createEnvironmentView;

@Controller
@RequestMapping(Routes.ENVIRONMENT_HANDLER)
public class EnvironmentHandler {

    @Resource(name = Context.ENV_NAME)
    Environment environment;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody EnvironmentView viewRoot() {
        return createEnvironmentView(environment);
    }

    @RequestMapping(method = RequestMethod.GET, value = Routes.ENV_VIEW)
    public @ResponseBody EnvironmentView viewEnvironment(@PathVariable String name) {
        return createEnvironmentView(environment.findEnvironment(name));
    }

    @RequestMapping(method = RequestMethod.PUT, value = Routes.ENV_CREATE_SUBENV)
    public @ResponseBody EnvironmentView addSubContainer(@PathVariable String name,@PathVariable String subName) {
        Environment env = environment.findEnvironment(name);
        env.createSubEnvironment(subName);
        return createEnvironmentView(env);
    }

    @RequestMapping(method = RequestMethod.PUT, value = Routes.ENV_CREATE_APP)
    public @ResponseBody EnvironmentView addApplication(@PathVariable String name,@PathVariable String appName) {
        Environment env = environment.findEnvironment(name);
        env.createApplication(appName);
        return createEnvironmentView(env);
    }


}
