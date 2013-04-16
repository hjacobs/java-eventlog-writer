package de.zalando.zomcat.jobs.listener;

import org.apache.log4j.Logger;
import org.apache.log4j.NDC;

import org.junit.Test;

import junit.framework.Assert;

public class JobFlowIdListenerTest {
    private static final Logger LOG = Logger.getLogger(JobFlowIdListenerTest.class);

    private final JobFlowIdListener jobFlowIdListener = new JobFlowIdListener();

    @Test
    public void testJobFlowId() throws Exception {
        LOG.debug("Message with empty flowId");

        Assert.assertEquals("There should be no diagnostic messages defined.", 0, NDC.getDepth());

        jobFlowIdListener.onExecutionSetUp(null, null, "host");
        Assert.assertEquals("There should be one diagnostic messages defined.", 1, NDC.getDepth());

        LOG.debug("Message with generated UUID as flowId");

        jobFlowIdListener.onExecutionTearDown(null);
        Assert.assertEquals("There should be no diagnostic messages defined.", 0, NDC.getDepth());

        LOG.debug("Message without UUID as flowId - removed flowId.");
    }

    @Test
    public void testNestedJobFlowId() throws Exception {
        LOG.debug("Message with empty flowId");

        Assert.assertEquals("There should be no diagnostic messages defined.", 0, NDC.getDepth());

        jobFlowIdListener.onExecutionSetUp(null, null, "host");
        Assert.assertEquals("There should be one diagnostic messages defined.", 1, NDC.getDepth());

        LOG.debug("Message with one generated UUID as flowId");

        jobFlowIdListener.onExecutionSetUp(null, null, "host");
        Assert.assertEquals("There should be one diagnostic messages defined - the other must be cleaned...", 1,
            NDC.getDepth());

        LOG.debug("Message with two generated UUID as flowId");

        jobFlowIdListener.onExecutionTearDown(null);
        Assert.assertEquals("There should be no diagnostic messages defined.", 0, NDC.getDepth());

        LOG.debug("Message with one generated UUID as flowId");

        jobFlowIdListener.onExecutionTearDown(null);
        Assert.assertEquals("There should be no diagnostic messages defined.", 0, NDC.getDepth());

        LOG.debug("Message without UUID as flowId - removed flowId.");
    }

}
