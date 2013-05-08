package service.environment;

import model.Environment;

public interface EnvironmentService {

    Environment getCurrentEnvironment();


    void update(Environment environment);

}
