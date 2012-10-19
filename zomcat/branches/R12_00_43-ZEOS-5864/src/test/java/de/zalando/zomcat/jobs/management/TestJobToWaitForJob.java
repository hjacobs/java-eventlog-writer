package de.zalando.zomcat.jobs.management;

import org.quartz.JobExecutionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.zomcat.jobs.AbstractJob;
import de.zalando.zomcat.jobs.JobConfig;

public class TestJobToWaitForJob extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(TestJobToWaitForJob.class);

    @Override
    public void doRun(final JobExecutionContext context, final JobConfig config) throws Exception {
        LOG.info("Simulating running Job. Sleeping for 5 Seconds");
        Thread.sleep(5000);
    }

    @Override
    public String getDescription() {
        return "TestjobToWaitFor";
    }

}
