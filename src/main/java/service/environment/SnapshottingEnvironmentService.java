package service.environment;

import model.Environment;
import service.history.EnvironmentSnapshotService;
import service.history.EnvironmentSnapshot;

import javax.annotation.Resource;

public class SnapshottingEnvironmentService implements EnvironmentService {

    @Resource
    EnvironmentSnapshotService service;


    @Override
    public Environment getCurrentEnvironment() {
        EnvironmentSnapshot snapshot = service.getLatest();
        if (snapshot == null) {
            return Environment.createRootEnvironment();
        }
        return EnvironmentSnapshot.environmentFor(snapshot);
    }


    @Override
    public void update(Environment environment) {
        service.recordSnapshot(environment);
    }
}
