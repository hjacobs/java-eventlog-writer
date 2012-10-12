package de.zalando.zomcat.jobs.management;

import org.quartz.JobExecutionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.zomcat.jobs.AbstractJob;
import de.zalando.zomcat.jobs.JobConfig;

public class TestJob1Job extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(TestJob1Job.class);

    @Override
    public void doRun(final JobExecutionContext context, final JobConfig config) throws Exception {
        LOG.info("Doing something");
    }

    @Override
    public String getDescription() {
        return "Testjob 1";
    }

}
