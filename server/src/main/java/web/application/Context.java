package web.application;

import com.google.common.base.Strings;
import com.google.common.io.Files;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import service.environment.EnvironmentService;
import service.environment.SnapshottingEnvironmentService;
import service.history.EnvironmentSnapshotService;
import service.history.FileSystemEnvironmentSnapshotService;

import java.io.File;

@Configuration
@EnableWebMvc
@ComponentScan
public class Context {

    public static final String ENV_NAME = "env";

    public static final String ROOT_DIR = "root.dir";

    private static final String ROOT_DIR_PROP = "#{systemProperties['" + ROOT_DIR + "']}";

    public static final String HISTORY_SERVICE_NAME = "history";

    @Value(ROOT_DIR_PROP)
    private String root;


    @Bean(autowire = Autowire.BY_NAME, name = ENV_NAME)
    public EnvironmentService getEnv() {
        return new SnapshottingEnvironmentService(historyService());
    }

    @Bean(autowire = Autowire.BY_NAME, name = HISTORY_SERVICE_NAME)
    public EnvironmentSnapshotService historyService() {
        if (Strings.isNullOrEmpty(root)) {
            return new FileSystemEnvironmentSnapshotService(Files.createTempDir());
        }
        else {
            File rootDir = new File(root);
            if (!rootDir.exists()) {
                throw new RuntimeException("provided rootdir did not exist");
            }
            else if (!rootDir.isDirectory()) {
                throw new RuntimeException("provided rootdir is not a directory");
            }
            else if (!rootDir.canWrite()) {
                throw new RuntimeException("provided rootdir is not writable");
            }
            return new FileSystemEnvironmentSnapshotService(rootDir);
        }
    }
}
