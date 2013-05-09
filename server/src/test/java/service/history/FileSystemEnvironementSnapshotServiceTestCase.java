package service.history;

import static org.junit.Assert.assertEquals;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.junit.Test;
import service.environment.model.Environment;
import service.history.model.EnvironmentSnapshot;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FileSystemEnvironementSnapshotServiceTestCase {

    public static final String SNAPSHOT_JSON =
            "{\n  \"root\" : {\n    \"subEnvironments\" : [ ],\n    \"applicationNodes\" : [ ],\n    \"name\" : \"root\",\n    \"properties\" : { }\n  }\n}";


    @Test
    public void testSave() throws IOException {
        Environment rootEnvironment = Environment.createRootEnvironment();
        File tempDir = Files.createTempDir();
        FileSystemEnvironmentSnapshotService service = new FileSystemEnvironmentSnapshotService(tempDir);
        service.recordSnapshot(EnvironmentSnapshot.snapshotOf(rootEnvironment));
        List<File> files = getFiles(tempDir);
        String s = Files.toString(Iterables.getOnlyElement(files), Charsets.UTF_8);
        assertEquals(SNAPSHOT_JSON, s);
    }


    @Test
    public void testIdentifierMatchesFileName() throws IOException {
        Environment rootEnvironment = Environment.createRootEnvironment();
        File tempDir = Files.createTempDir();
        FileSystemEnvironmentSnapshotService service = new FileSystemEnvironmentSnapshotService(tempDir);
        SnapshotId snapshotId = service.recordSnapshot(EnvironmentSnapshot.snapshotOf(rootEnvironment));
        List<File> files = getFiles(tempDir);
        File onlyElement = Iterables.getOnlyElement(files);
        assertEquals(onlyElement.getName().substring(11), "" + snapshotId.longValue());
    }


    @Test
    public void testRetrievingExistingSnapshot() throws IOException {
        Environment rootEnvironment = Environment.createRootEnvironment();
        File tempDir = Files.createTempDir();
        FileSystemEnvironmentSnapshotService service = new FileSystemEnvironmentSnapshotService(tempDir);
        SnapshotId snapshotId = service.recordSnapshot(EnvironmentSnapshot.snapshotOf((rootEnvironment)));
        EnvironmentSnapshot snapshot = service.get(snapshotId);
        assertEquals(snapshot, EnvironmentSnapshot.snapshotOf(rootEnvironment));
    }


    @Test
    public void testRetrievingLatestSnapshot() throws IOException {
        Environment rootEnvironment = Environment.createRootEnvironment();
        Environment latestEnv = Environment.createRootEnvironment();
        latestEnv.put("a", "b");
        File tempDir = Files.createTempDir();
        FileSystemEnvironmentSnapshotService service = new FileSystemEnvironmentSnapshotService(tempDir);
        service.recordSnapshot(EnvironmentSnapshot.snapshotOf(rootEnvironment));
        service.recordSnapshot(EnvironmentSnapshot.snapshotOf(latestEnv));
        EnvironmentSnapshot snapshot = service.getLatest();
        assertEquals(snapshot, EnvironmentSnapshot.snapshotOf(latestEnv));
    }


    @Test
    public void testRetrievingAllSnapshots() throws IOException {
        Environment rootEnvironment = Environment.createRootEnvironment();
        File tempDir = Files.createTempDir();
        FileSystemEnvironmentSnapshotService service = new FileSystemEnvironmentSnapshotService(tempDir);
        service.recordSnapshot(EnvironmentSnapshot.snapshotOf((rootEnvironment)));
        Collection<EnvironmentSnapshot> allSnapshots = service.getAllSnapshots();
        assertEquals(Iterables.getOnlyElement(allSnapshots), EnvironmentSnapshot.snapshotOf(rootEnvironment));
    }


    @Test
    public void testExistingFilesLoaded() throws IOException {
        Environment rootEnvironment = Environment.createRootEnvironment();
        File tempDir = Files.createTempDir();
        File existingProperties = new File(tempDir, "properties-1");
        Files.write(SNAPSHOT_JSON, existingProperties, Charsets.UTF_8);
        FileSystemEnvironmentSnapshotService service = new FileSystemEnvironmentSnapshotService(tempDir);
        EnvironmentSnapshot snapshot = service.get(new SnapshotId(1l));
        assertEquals(snapshot, EnvironmentSnapshot.snapshotOf(rootEnvironment));
    }


    private List<File> getFiles(File tempDir) {
        File[] files = tempDir.listFiles();
        return files != null ? Arrays.asList(files) : Lists.<File>newArrayList();
    }

}
