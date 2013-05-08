package service.environment;

import service.environment.model.Environment;
import service.history.EnvironmentSnapshotService;
import service.history.model.EnvironmentSnapshot;

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
        service.recordSnapshot(EnvironmentSnapshot.snapshotOf(environment));
    }
}
