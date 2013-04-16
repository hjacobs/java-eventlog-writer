package de.zalando.zomcat.jobs;

import org.quartz.JobExecutionContext;

public class TestResourceLockJob extends AbstractJob {

    @Override
    public void doRun(final JobExecutionContext context, final JobConfig config) throws Exception {
        // nothing to do here
    }

    @Override
    public String getDescription() {
        return "resource lock test";
    }

    @Override
    protected String getLockResource() {

        // force job locking
        return this.getBeanName();
    }

    @Override
    protected String getAppInstanceKey() {
        return "local";
    }

}
