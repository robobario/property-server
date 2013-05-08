package web.application;

import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import service.environment.EnvironmentService;
import service.environment.SnapshottingEnvironmentService;
import service.history.EnvironmentSnapshotService;
import service.history.FileSystemEnvironmentSnapshotService;

@Configuration
@EnableWebMvc
@ComponentScan
public class Context {

    public static final String ENV_NAME = "env";


    @Bean(autowire = Autowire.BY_NAME, name = ENV_NAME)
    public EnvironmentService getEnv() {
        return new SnapshottingEnvironmentService(historyService());
    }


    public EnvironmentSnapshotService historyService() {
        FileSystemEnvironmentSnapshotService service =
                new FileSystemEnvironmentSnapshotService(Files.createTempDir());
        return service;
    }
}
