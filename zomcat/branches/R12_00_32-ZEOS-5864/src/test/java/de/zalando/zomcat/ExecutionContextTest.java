package de.zalando.zomcat;

import org.junit.Test;

import junit.framework.Assert;

public class ExecutionContextTest {
    @Test
    public void testExecutionContext1() throws Exception {
        Assert.assertTrue(ExecutionContext.isEmpty());
    }

    @Test
    public void testExecutionContext2() throws Exception {
        testExecutionContext1();
        ExecutionContext.add("a", "1");
        Assert.assertFalse(ExecutionContext.isEmpty());
        Assert.assertEquals(ExecutionContext.getValue("a"), "1");
        Assert.assertEquals(ExecutionContext.serialize(), "a=1");
        ExecutionContext.clear();
        Assert.assertTrue(ExecutionContext.isEmpty());
    }

    @Test
    public void testExecutionContext3() throws Exception {
        testExecutionContext1();
        ExecutionContext.add("a", "1");
        ExecutionContext.add("b", "2");
        Assert.assertFalse(ExecutionContext.isEmpty());
        Assert.assertEquals(ExecutionContext.getValue("a"), "1");
        Assert.assertEquals(ExecutionContext.getValue("b"), "2");
        Assert.assertTrue(ExecutionContext.serialize().contains("a=1"));
        Assert.assertTrue(ExecutionContext.serialize().contains("b=2"));
        Assert.assertTrue(ExecutionContext.serialize().contains("&"));
        ExecutionContext.clear();
        Assert.assertTrue(ExecutionContext.isEmpty());
    }

    @Test
    public void testExecutionContext4() throws Exception {
        testExecutionContext1();
        ExecutionContext.addSerialized("a=1&b=2");
        Assert.assertFalse(ExecutionContext.isEmpty());
        Assert.assertEquals(ExecutionContext.getValue("a"), "1");
        Assert.assertEquals(ExecutionContext.getValue("b"), "2");
        Assert.assertTrue(ExecutionContext.serialize().contains("a=1"));
        Assert.assertTrue(ExecutionContext.serialize().contains("b=2"));
        Assert.assertTrue(ExecutionContext.serialize().contains("&"));
        ExecutionContext.clear();
        Assert.assertTrue(ExecutionContext.isEmpty());
    }

    @Test
    public void testExecutionContext5() throws Exception {
        testExecutionContext1();
        ExecutionContext.addSerialized("");
        Assert.assertTrue(ExecutionContext.isEmpty());
    }

    @Test
    public void testExecutionContext6() throws Exception {
        testExecutionContext1();
        ExecutionContext.addSerialized(null);
        Assert.assertTrue(ExecutionContext.isEmpty());
    }

    @Test
    public void testExecutionContext7() throws Exception {
        testExecutionContext1();
        ExecutionContext.addSerialized("a=1&b=2&c=3&d=4");
        Assert.assertEquals(ExecutionContext.getValue("a"), "1");
        Assert.assertEquals(ExecutionContext.getValue("b"), "2");
        Assert.assertEquals(ExecutionContext.getValue("c"), "3");
        Assert.assertEquals(ExecutionContext.getValue("d"), "4");
        Assert.assertTrue(ExecutionContext.serialize().contains("a=1"));
        Assert.assertTrue(ExecutionContext.serialize().contains("b=2"));
        Assert.assertTrue(ExecutionContext.serialize().contains("c=3"));
        Assert.assertTrue(ExecutionContext.serialize().contains("d=4"));
        Assert.assertTrue(ExecutionContext.serialize().contains("&"));
        ExecutionContext.clear();
        Assert.assertTrue(ExecutionContext.isEmpty());
    }

    @Test
    public void testExecutionContext8() throws Exception {
        testExecutionContext1();
        Assert.assertEquals("", ExecutionContext.serialize());
    }
}
