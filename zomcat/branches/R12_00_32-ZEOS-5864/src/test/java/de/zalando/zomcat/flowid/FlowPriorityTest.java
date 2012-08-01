package de.zalando.zomcat.flowid;

import org.junit.Test;

import de.zalando.zomcat.flowid.FlowPriority.Priority;

import junit.framework.Assert;

public class FlowPriorityTest {
    @Test
    public void testFlowPriority1() throws Exception {
        FlowPriority.setFlowPriority(Priority.DEFAULT);
        Assert.assertEquals(Priority.DEFAULT, FlowPriority.flowPriority());
    }

    @Test
    public void testFlowPriority2() throws Exception {
        FlowPriority.setFlowPriority(Priority.HIGH);
        Assert.assertEquals(Priority.HIGH, FlowPriority.flowPriority());
    }

    @Test
    public void testFlowPriority3() throws Exception {
        FlowPriority.clearFlowPriority();
        Assert.assertEquals(Priority.DEFAULT, FlowPriority.flowPriority());
    }

    @Test
    public void testFlowPriority4() throws Exception {
        testFlowPriority2();
        FlowPriority.setFlowPriority(Priority.DEFAULT);

        // we cannot reduce the priority!
        Assert.assertEquals(Priority.HIGH, FlowPriority.flowPriority());
    }

    @Test
    public void testFlowPriority5() throws Exception {
        testFlowPriority4();
        FlowPriority.clearFlowPriority();
        testFlowPriority1();
    }
}
