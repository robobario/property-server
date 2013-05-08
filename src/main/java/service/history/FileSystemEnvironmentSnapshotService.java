package service.history;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import com.google.common.io.Files;
import com.google.common.primitives.Longs;
import model.Environment;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileSystemEnvironmentSnapshotService implements EnvironmentSnapshotService {

    private final File rootDir;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ImmutableMap<SnapshotId, EnvironmentSnapshot> cache = ImmutableMap.of();

    private static Pattern filenamePattern = Pattern.compile("properties-([0-9]+)");


    public FileSystemEnvironmentSnapshotService(File rootDir) {
        this.rootDir = rootDir;
        loadPreExistingProperties(rootDir);
    }


    private void loadPreExistingProperties(File rootDir) {
        File[] files = rootDir.listFiles();
        if (files != null) {
            for (File file : files) {
                addToCacheIfIsPropertiesFile(file);
            }
        }
    }


    private void addToCacheIfIsPropertiesFile(File file) {
        try {
            Matcher matcher = filenamePattern.matcher(file.getName());
            if (matcher.matches()) {
                EnvironmentSnapshot snapshot = objectMapper.reader(EnvironmentSnapshot.class).readValue(file);
                addSnapshotToCache(new SnapshotId(Long.parseLong(matcher.group(1))), snapshot);
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }


    @Override
    public SnapshotId recordSnapshot(Environment environment) {
        SnapshotId snapshotId = createSnapshotId();
        File file = createNewSnapshotFileInRootDir(snapshotId);
        EnvironmentSnapshot snapshot = EnvironmentSnapshot.snapshotOf(environment);
        recordSnapshotInFile(snapshot, file);
        addSnapshotToCache(snapshotId, snapshot);
        return snapshotId;
    }


    @Override
    public EnvironmentSnapshot get(SnapshotId snapshotId) {
        if (cache.containsKey(snapshotId)) {
            return cache.get(snapshotId);
        }
        else {
            throw new RuntimeException("no such snapshot : " + snapshotId);
        }
    }


    @Override
    public Collection<EnvironmentSnapshot> getAllSnapshots() {
        return cache.values();
    }


    @Override
    public EnvironmentSnapshot getLatest() {
        List<SnapshotId> snapshotIds = Ordering.from(new Comparator<SnapshotId>() {
            @Override
            public int compare(SnapshotId o, SnapshotId o2) {
                return Longs.compare(o2.longValue(), o.longValue());
            }
        }).sortedCopy(cache.keySet());
        if (!snapshotIds.isEmpty()) {
            return cache.get(snapshotIds.get(0));
        }
        else {
            return null;
        }
    }


    private void addSnapshotToCache(SnapshotId snapshotId, EnvironmentSnapshot snapshot) {
        cache = ImmutableMap.<SnapshotId, EnvironmentSnapshot>builder().putAll(cache).put(snapshotId, snapshot).build();
    }


    private SnapshotId createSnapshotId() {
        return new SnapshotId(new Date().getTime());
    }


    private File createNewSnapshotFileInRootDir(SnapshotId snapshotId) {
        return new File(rootDir, "properties-" + snapshotId.longValue());
    }


    private void recordSnapshotInFile(EnvironmentSnapshot s, File file) {
        try {
            Files.write(createSnapshotAsString(s), file, Charsets.UTF_8);
        }
        catch (IOException e) {
            throw new RuntimeException("failed to write snapshot", e);
        }
    }


    private String createSnapshotAsString(EnvironmentSnapshot snapshot) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(snapshot);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("failed to create snapshot for environment", e);
        }
    }
}
