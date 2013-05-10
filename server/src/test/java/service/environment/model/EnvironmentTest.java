package service.environment.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

public class EnvironmentTest {

    public static final String KEY = "property";


    @Test
    public void testGetApplication_NoApplications() {
        Environment root = Environment.createRootEnvironment();
        Application ih2 = root.getApplication("ih2");
        assertNull(ih2);
    }


    @Test
    public void testGetApplication_ApplicationAtRootLevel() {
        Environment root = Environment.createRootEnvironment();
        Application ih2 = root.createApplication("ih2");
        assertEquals(ih2, root.getApplication("ih2"));
    }


    @Test
    public void testGetApplication_ThroughSubContainer() {
        Environment root = Environment.createRootEnvironment();
        Environment subContainer = root.createSubEnvironment("impression-handlers");
        Application ih2 = subContainer.createApplication("ih2");
        assertEquals(ih2, root.getApplication("ih2"));
    }


    @Test
    public void testGetAllApplications_NoApplications() {
        Environment root = Environment.createRootEnvironment();
        assertTrue(root.getAllApplications().isEmpty());
    }


    @Test
    public void testGetAllApplications() {
        Environment root = Environment.createRootEnvironment();
        Application ih2 = root.createApplication("ih2");
        assertEquals(ImmutableMap.of("ih2", ih2), root.getAllApplications());
    }


    @Test
    public void testGetAllApplications_CollectsSubApplications() {
        Environment root = Environment.createRootEnvironment();
        Application ih2 = root.createApplication("ih2");
        Environment subContainer = root.createSubEnvironment("sharding-servers");
        Application sh1 = subContainer.createApplication("sh1");

        assertEquals(ImmutableMap.of("ih2", ih2, "sh1", sh1), root.getAllApplications());
    }


    @Test
    public void testPropertyGetWhenEmpty() {
        Environment root = Environment.createRootEnvironment();
        assertNull(root.get(KEY));
    }


    @Test
    public void testPropertyGet() {
        Environment root = Environment.createRootEnvironment();
        root.put(KEY, "a");
        assertEquals("a", root.get(KEY));
    }


    @Test
    public void testPropertyOverwrite() {
        Environment root = Environment.createRootEnvironment();
        root.put(KEY, "a");
        root.put(KEY, "b");
        assertEquals("b", root.get(KEY));
    }


    @Test
    public void testGetLocalPropertiesDoesNotIncludeParentProperties() {
        Environment root = Environment.createRootEnvironment();
        root.put(KEY, "a");
        Application ih2 = root.createApplication("ih2");
        ih2.put("p2", "b");
        assertEquals(ImmutableMap.of("p2", "b"), ih2.getLocalProperties());
    }


    @Test
    public void testGetLocalProperties() {
        Environment root = Environment.createRootEnvironment();
        root.put(KEY, "a");
        assertEquals(ImmutableMap.of(KEY, "a"), root.getAllProperties());
    }


    @Test
    public void testPropertyGetFromParent_Environment() {
        Environment root = Environment.createRootEnvironment();
        Environment subContainer = root.createSubEnvironment("sharding-servers");
        root.put(KEY, "a");
        assertEquals("a", subContainer.get(KEY));
    }


    @Test
    public void testPropertyGetFromParent_Application() {
        Environment root = Environment.createRootEnvironment();
        Application application = root.createApplication("sh1");
        root.put(KEY, "a");
        assertEquals("a", application.get(KEY));
    }


    @Test
    public void testPropertyGetOverrideParent_Environment() {
        Environment root = Environment.createRootEnvironment();
        Environment subContainer = root.createSubEnvironment("sharding-servers");
        root.put(KEY, "a");
        subContainer.put(KEY, "b");
        assertEquals("b", subContainer.get(KEY));
    }


    @Test
    public void testPropertyGetOverrideParent_Application() {
        Environment root = Environment.createRootEnvironment();
        Application application = root.createApplication("sh1");
        root.put(KEY, "a");
        application.put(KEY, "b");
        assertEquals("b", application.get(KEY));
    }
}
