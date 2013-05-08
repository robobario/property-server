package web.application;

import service.environment.model.Application;
import service.environment.model.Environment;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import service.history.EnvironmentSnapshotService;
import service.history.FileSystemEnvironmentSnapshotService;

import java.io.File;

@Configuration
@EnableWebMvc
public class Context {

    public static final String ENV_NAME = "env";

    private static final String HISTORY_SERVICE_NAME = "historyService";


    @Bean(autowire = Autowire.BY_NAME, name = ENV_NAME)
    public Environment getEnv() {
        Environment rootEnvironment = Environment.createRootEnvironment();
        Application app1 = rootEnvironment.createApplication("app1");
        Environment sub = rootEnvironment.createSubEnvironment("sub");
        rootEnvironment.put("p1", "a");
        rootEnvironment.put("p2", "b");
        app1.put("p1", "d");
        app1.put("p3", "c");
        sub.put("p1", "e");
        Application app2 = sub.createApplication("app2");
        app2.put("p4", "f");
        app2.put("p1", "g");
        app2.put("p5", "${p1}${p2}");
        return rootEnvironment;
    }


    @Bean(autowire = Autowire.BY_NAME, name = HISTORY_SERVICE_NAME)
    public EnvironmentSnapshotService historyService() {
        FileSystemEnvironmentSnapshotService service =
                new FileSystemEnvironmentSnapshotService(new File("/tmp"));
        return service;
    }
}
