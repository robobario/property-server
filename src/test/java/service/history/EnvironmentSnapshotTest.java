package service.history;

import static model.Environment.createRootEnvironment;
import static org.junit.Assert.assertEquals;
import static service.history.EnvironmentSnapshot.snapshotOf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import model.Application;
import model.Environment;
import org.junit.Test;

import java.io.IOException;

public class EnvironmentSnapshotTest {
    @Test
    public void testEnvSnapshotting() {
        Environment env = createRootEnvironment();
        env.createApplication("app1");
        EnvironmentSnapshot environmentSnapshot = snapshotOf(env);
        EnvironmentSnapshot expected = new EnvironmentSnapshot(new EnvironmentNode(ImmutableSet.<EnvironmentNode>of(), ImmutableSet.of(new PropertyNode("app1", ImmutableMap.<String, String>of())), "root", ImmutableMap.<String, String>of()));
        assertEquals(expected, environmentSnapshot);
    }

    @Test
    public void testEnvSnapshottingTransfersAppProps() {
        Environment env = createRootEnvironment();
        Application app1 = env.createApplication("app1");
        app1.put("a", "b");
        EnvironmentSnapshot environmentSnapshot = snapshotOf(env);
        EnvironmentSnapshot expected = new EnvironmentSnapshot(new EnvironmentNode(ImmutableSet.<EnvironmentNode>of(), ImmutableSet.of(new PropertyNode("app1", ImmutableMap.<String, String>of("a", "b"))), "root", ImmutableMap.<String, String>of()));
        assertEquals(expected, environmentSnapshot);
    }

    @Test
    public void testEnvSnapshottingTransfersSubEnv() {
        Environment env = createRootEnvironment();
        env.createSubEnvironment("sub");
        EnvironmentSnapshot environmentSnapshot = snapshotOf(env);
        EnvironmentSnapshot expected = new EnvironmentSnapshot(new EnvironmentNode(ImmutableSet.<EnvironmentNode>of(new EnvironmentNode(ImmutableSet.<EnvironmentNode>of(), ImmutableSet.<PropertyNode>of(), "sub", ImmutableMap.<String, String>of())), ImmutableSet.<PropertyNode>of(), "root", ImmutableMap.<String, String>of()));
        assertEquals(expected, environmentSnapshot);
    }

    @Test
    public void testEnvSnapshottingTransfersSubEnvProps() {
        Environment env = createRootEnvironment();
        Environment sub = env.createSubEnvironment("sub");
        sub.put("a", "b");
        EnvironmentSnapshot environmentSnapshot = snapshotOf(env);
        EnvironmentSnapshot expected = new EnvironmentSnapshot(new EnvironmentNode(ImmutableSet.<EnvironmentNode>of(new EnvironmentNode(ImmutableSet.<EnvironmentNode>of(), ImmutableSet.<PropertyNode>of(), "sub", ImmutableMap.<String, String>of("a", "b"))), ImmutableSet.<PropertyNode>of(), "root", ImmutableMap.<String, String>of()));
        assertEquals(expected, environmentSnapshot);
    }

    @Test
    public void testEnvSnapshottingTransfersProperties() {
        Environment env = createRootEnvironment();
        env.put("a", "b");
        EnvironmentSnapshot environmentSnapshot = snapshotOf(env);
        EnvironmentSnapshot expected = new EnvironmentSnapshot(new EnvironmentNode(ImmutableSet.<EnvironmentNode>of(), ImmutableSet.<PropertyNode>of(), "root", ImmutableMap.<String, String>of("a", "b")));
        assertEquals(expected, environmentSnapshot);
    }

    @Test
    public void testSnapshotToEnv() {
        EnvironmentSnapshot expected = new EnvironmentSnapshot(new EnvironmentNode(ImmutableSet.<EnvironmentNode>of(), ImmutableSet.of(new PropertyNode("app1", ImmutableMap.<String, String>of("a", "b"))), "root", ImmutableMap.<String, String>of()));
        Environment environment = EnvironmentSnapshot.environmentFor(expected);
        Application onlyElement = Iterables.getOnlyElement(environment.getLocalApplications());
        assertEquals(onlyElement.getName(), "app1");
        assertEquals(environment.getName(), "root");
        assertEquals(ImmutableMap.of("a", "b"), onlyElement.getLocalProperties());
    }

    @Test
    public void testSnapshotSerialization() throws IOException {
        EnvironmentSnapshot expected = new EnvironmentSnapshot(new EnvironmentNode(ImmutableSet.<EnvironmentNode>of(), ImmutableSet.of(new PropertyNode("app1", ImmutableMap.<String, String>of("a", "b"))), "root", ImmutableMap.<String, String>of()));
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writer().writeValueAsString(expected);
        Object obj = mapper.reader(EnvironmentSnapshot.class).readValue(json);
        assertEquals(expected,obj);
    }


}
