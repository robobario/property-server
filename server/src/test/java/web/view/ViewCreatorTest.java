package web.view;

import static org.junit.Assert.assertEquals;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import service.environment.model.Application;
import service.environment.model.Environment;

public class ViewCreatorTest {

    @Test
    public void testCreateTransfersEnvName(){
        Environment rootEnvironment = Environment.createRootEnvironment();
        EnvironmentView view = ViewCreator.createEnvironmentView(rootEnvironment);
        assertEquals(rootEnvironment.getName(), view.getName());
    }

    @Test
    public void testCreateTransfersProperties(){
        Environment rootEnvironment = Environment.createRootEnvironment();
        rootEnvironment.put("p1","a");
        rootEnvironment.put("p2","b");
        EnvironmentView view = ViewCreator.createEnvironmentView(rootEnvironment);
        assertEquals(ImmutableList.of(PropertyView.local("p1","a","a", view),PropertyView.local("p2","b","b", view)), view.getProperties());
    }

    @Test
    public void testCreateTransfersDerivedProperties(){
        Environment rootEnvironment = Environment.createRootEnvironment();
        rootEnvironment.put("p1","a");
        rootEnvironment.put("p2","${p1}");
        EnvironmentView view = ViewCreator.createEnvironmentView(rootEnvironment);
        assertEquals(ImmutableList.of(PropertyView.local("p1","a","a", view),PropertyView.local("p2","${p1}","a", view)), view.getProperties());
    }


    @Test
    public void testCreateTransfersApplication(){
        Environment rootEnvironment = Environment.createRootEnvironment();
        rootEnvironment.createApplication("bapp1");
        rootEnvironment.createApplication("app2");
        EnvironmentView environmentView = ViewCreator.createEnvironmentView(rootEnvironment);
        assertEquals(ImmutableList.of(ApplicationView.create("app2"), ApplicationView.create("bapp1")), environmentView.getApplications());
    }

    @Test
    public void testCreateTransfersApplicationProperties(){
        Environment rootEnvironment = Environment.createRootEnvironment();
        Application app1 = rootEnvironment.createApplication("app1");
        app1.put("p1","p2");
        EnvironmentView environmentView = ViewCreator.createEnvironmentView(rootEnvironment);
        ApplicationView appV = ApplicationView.create("app1");
        appV.addLocalProp("p1","p2","p2");
        assertEquals(ImmutableList.of(appV), environmentView.getApplications());
    }

    @Test
    public void testCreateTransfersApplicationPropertyOverrides(){
        Environment rootEnvironment = Environment.createRootEnvironment();
        rootEnvironment.put("p2","b");
        Application app1 = rootEnvironment.createApplication("app1");
        app1.put("p1","a");
        EnvironmentView environmentView = ViewCreator.createEnvironmentView(rootEnvironment);
        ApplicationView appV = ApplicationView.create("app1");
        appV.addLocalProp("p1","a","a");
        appV.addInheritedProp("p2", "b","b");
        assertEquals(ImmutableList.of(appV),environmentView.getApplications());
    }

    @Test
    public void testCreateTransfersSubEnvironment(){
        Environment rootEnvironment = Environment.createRootEnvironment();
        rootEnvironment.createSubEnvironment("env2");
        EnvironmentView envView = EnvironmentView.create("env2");
        EnvironmentView actualView = ViewCreator.createEnvironmentView(rootEnvironment);
        assertEquals(ImmutableList.of(envView),actualView.getSubEnvironmentViews());
    }

    @Test
    public void testCreateTransfersSubEnvironmentProperties(){
        Environment rootEnvironment = Environment.createRootEnvironment();
        Environment env = rootEnvironment.createSubEnvironment("env1");
        env.put("p1", "p2");
        EnvironmentView expected = EnvironmentView.create("env1");
        expected.addLocalProp("p1","p2","p2");
        EnvironmentView environmentView = ViewCreator.createEnvironmentView(rootEnvironment);
        assertEquals(ImmutableList.of(expected), environmentView.getSubEnvironmentViews());
    }

    @Test
    public void testCreateTransfersSubEnvironmentPropertyOverrides(){
        Environment rootEnvironment = Environment.createRootEnvironment();
        rootEnvironment.put("p2","b");
        Environment env = rootEnvironment.createSubEnvironment("env");
        env.put("p1", "a");
        EnvironmentView environmentView = ViewCreator.createEnvironmentView(rootEnvironment);
        EnvironmentView envV = EnvironmentView.create("env");
        envV.addLocalProp("p1","a","a");
        envV.addInheritedProp("p2", "b","b");
        assertEquals(ImmutableList.of(envV),environmentView.getSubEnvironmentViews());
    }
}
