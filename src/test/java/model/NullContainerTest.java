package model;

import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class NullContainerTest {

    @Test
    public void excitingNullContainerTest(){
        NullContainer container = new NullContainer();
        assertTrue(container.getAllProperties().isEmpty());
        assertTrue(container.getAllPropertyKeys().isEmpty());
        assertNull(container.get("anyProp"));
    }

    @Test(expected = RuntimeException.class)
    public void puttingCausesExplosions(){
        NullContainer container = new NullContainer();
        container.put("p1","p2");
    }


}
