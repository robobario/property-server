package model;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PropertyReplacingContainerTest {

    @Test
    public void testGet_WithReplacement() {
        PropertyContainer container = PropertyReplacingContainer.decorate(new InMemoryPropertyContainer());
        container.put("p1", "a");
        container.put("p2", "${p1}");
        assertEquals("a", container.get("p2"));
    }

    @Test
    public void testGet_WithReplacement_MultipleReplacements() {
        PropertyContainer container = PropertyReplacingContainer.decorate(new InMemoryPropertyContainer());
        container.put("p1", "a");
        container.put("p2", "b");
        container.put("p3", "${p1}${p2}");
        assertEquals("ab", container.get("p3"));
    }

    @Test
    public void testGet_WithReplacement_MultipleReplacements_WithOtherCharacters() {
        PropertyContainer container = PropertyReplacingContainer.decorate(new InMemoryPropertyContainer());
        container.put("p1", "a");
        container.put("p2", "b");
        container.put("p3", "@${p1}--${p2}C");
        assertEquals("@a--bC", container.get("p3"));
    }

    @Test
    public void testGet_WithNoReplacement() {
        PropertyContainer container = PropertyReplacingContainer.decorate(new InMemoryPropertyContainer());
        container.put("p1", "a");
        assertEquals("a", container.get("p1"));
    }

    @Test
    public void testReplacementInAllProperties_NoReplacements() {
        PropertyContainer container = PropertyReplacingContainer.decorate(new InMemoryPropertyContainer());
        container.put("p1", "a");
        assertEquals(ImmutableMap.of("p1", "a"), container.getAllProperties());
    }

    @Test
    public void testReplacementInAllProperties() {
        PropertyContainer container = PropertyReplacingContainer.decorate(new InMemoryPropertyContainer());
        container.put("p1", "a");
        container.put("p2", "${p1}");
        assertEquals(ImmutableMap.of("p2", "a", "p1", "a"), container.getAllProperties());
    }


    @Test
    public void testReplacement_NoMatch() {
        PropertyContainer container = PropertyReplacingContainer.decorate(new InMemoryPropertyContainer());
        container.put("p2", "${p1}");
        assertEquals("${p1}", container.get("p2"));
    }

    @Test(expected = PropertyReplacementMaxDepthExceededException.class)
    public void testReplacement_LoopException() {
        PropertyContainer container = PropertyReplacingContainer.decorate(new InMemoryPropertyContainer());
        container.put("p1", "${p1}");
        container.get("p1");
    }
}
