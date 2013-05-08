package service.history;

import model.Environment;

import java.util.Collection;

public interface EnvironmentSnapshotService {

    public SnapshotId recordSnapshot(Environment environment);


    EnvironmentSnapshot get(SnapshotId snapshotId);


    public Collection<EnvironmentSnapshot> getAllSnapshots();


    EnvironmentSnapshot getLatest();

}
