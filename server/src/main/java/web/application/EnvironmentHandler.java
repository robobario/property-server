package web.application;

import static web.view.ViewCreator.createEnvironmentView;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.environment.EnvironmentService;
import service.environment.model.Environment;
import service.history.EnvironmentSnapshotService;
import service.history.model.EnvironmentSnapshot;
import web.view.EnvironmentView;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(Routes.ENVIRONMENT_HANDLER)
public class EnvironmentHandler {

    @Resource(name = Context.ENV_NAME)
    EnvironmentService service;

    @Resource(name = Context.HISTORY_SERVICE_NAME)
    EnvironmentSnapshotService snapshotService;


    @RequestMapping(method = RequestMethod.GET)
    public
    @ResponseBody
    EnvironmentView viewRoot(HttpServletResponse resp) {
        addAcal(resp);
        return createEnvironmentView(service.getCurrentEnvironment());
    }


    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    EnvironmentView updateCurrent(HttpServletResponse resp, @RequestBody EnvironmentSnapshot snapshot) {
        addAcal(resp);
        snapshotService.recordSnapshot(snapshot);
        return createEnvironmentView(service.getCurrentEnvironment());
    }


    @RequestMapping(method = RequestMethod.GET, value = Routes.ENV_VIEW)
    public
    @ResponseBody
    EnvironmentView viewEnvironment(HttpServletResponse resp, @PathVariable(Routes.ENV_NAME) String name) {
        addAcal(resp);
        return createEnvironmentView(service.getCurrentEnvironment().findEnvironment(name));
    }


    @RequestMapping(method = RequestMethod.PUT, value = Routes.ENV_CREATE_SUBENV)
    public
    @ResponseBody
    EnvironmentView addSubContainer(HttpServletResponse resp, @PathVariable(Routes.ENV_NAME) String name,
                                    @PathVariable(Routes.SUB_ENV_NAME) String subName) {
        addAcal(resp);
        Environment rootEnv = service.getCurrentEnvironment();
        ensureNewSubEnvNameUnique(subName, rootEnv);
        Environment env = rootEnv.findEnvironment(name);
        env.createSubEnvironment(subName);
        service.update(rootEnv);
        return createEnvironmentView(env);
    }


    @RequestMapping(method = RequestMethod.PUT, value = Routes.ENV_CREATE_APP)
    public
    @ResponseBody
    EnvironmentView addApplication(HttpServletResponse resp, @PathVariable(Routes.ENV_NAME) String name,
                                   @PathVariable(Routes.APP_NAME) String appName) {
        addAcal(resp);
        Environment rootEnv = service.getCurrentEnvironment();
        ensureNewApplicationNameUnique(appName, rootEnv);
        Environment env = rootEnv.findEnvironment(name);
        env.createApplication(appName);
        service.update(rootEnv);
        return createEnvironmentView(env);
    }


    @RequestMapping(method = RequestMethod.POST, value = Routes.ENV_PUT_PROPERTY)
    public
    @ResponseBody
    EnvironmentView addProperty(HttpServletResponse resp, @PathVariable(Routes.ENV_NAME) String name,
                                @RequestBody AddPropRequest request) {
        addAcal(resp);
        Environment rootEnv = service.getCurrentEnvironment();
        Environment env = rootEnv.findEnvironment(name);
        env.put(request.getKey(), request.getValue());
        service.update(rootEnv);
        return createEnvironmentView(env);
    }


    @RequestMapping(method = RequestMethod.DELETE, value = Routes.ENV_DELETE_PROPERTY)
    public
    @ResponseBody
    EnvironmentView remove(HttpServletResponse resp, @PathVariable(Routes.ENV_NAME) String name,
                           @PathVariable(Routes.PROPERTY_KEY) String propertyKey) {
        addAcal(resp);
        Environment rootEnv = service.getCurrentEnvironment();
        Environment env = rootEnv.findEnvironment(name);
        env.remove(propertyKey);
        service.update(env);
        return createEnvironmentView(rootEnv);
    }


    @RequestMapping(value = "/**", method = RequestMethod.OPTIONS)
    public void commonOptions(HttpServletResponse theHttpServletResponse) throws IOException {
        theHttpServletResponse
                .addHeader("Access-Control-Allow-Headers", "origin, content-type, accept, x-requested-with");
        theHttpServletResponse.addHeader("Access-Control-Max-Age",
                "60"); // seconds to cache preflight request --> less OPTIONS traffic
        theHttpServletResponse.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        addAcal(theHttpServletResponse);
    }


    private void ensureNewApplicationNameUnique(String appName, Environment rootEnv) {
        if (rootEnv.getApplication(appName) != null) {
            throw new ContainerUniquenessConstraintException();
        }
    }


    private void ensureNewSubEnvNameUnique(String subName, Environment rootEnv) {
        if (rootEnv.findEnvironment(subName) != null) {
            throw new ContainerUniquenessConstraintException();
        }
    }


    private void addAcal(HttpServletResponse theHttpServletResponse) {
        theHttpServletResponse.addHeader("Access-Control-Allow-Origin", "*");
    }

}
