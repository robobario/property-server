package service.environment;

import service.environment.model.Environment;

public interface EnvironmentService {

    Environment getCurrentEnvironment();


    void update(Environment environment);

}
