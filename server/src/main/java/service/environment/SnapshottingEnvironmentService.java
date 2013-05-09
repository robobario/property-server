package service.environment;

import service.environment.model.Environment;
import service.history.EnvironmentSnapshotService;
import service.history.model.EnvironmentSnapshot;

public class SnapshottingEnvironmentService implements EnvironmentService {

    private final EnvironmentSnapshotService service;


    public SnapshottingEnvironmentService(EnvironmentSnapshotService service) {
        this.service = service;
    }


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
