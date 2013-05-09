package service.history;

import service.history.model.EnvironmentSnapshot;

import java.util.Collection;

public interface EnvironmentSnapshotService {

    public SnapshotId recordSnapshot(EnvironmentSnapshot environment);


    EnvironmentSnapshot get(SnapshotId snapshotId);


    public Collection<EnvironmentSnapshot> getAllSnapshots();


    EnvironmentSnapshot getLatest();

}
