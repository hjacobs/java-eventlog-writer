package de.zalando.zomcat.jobs;

import org.quartz.JobExecutionContext;

public class TestJob4 extends AbstractJob {

    @Override
    public JobGroup getJobGroup() {
        return new JobGroup() {

            @Override
            public String groupName() {
                return "testGroup2";
            }
        };
    }

    @Override
    public void doRun(final JobExecutionContext context, final JobConfig config) throws Exception {
        // ignore
    }

    @Override
    public String getBeanName() {
        return "TestJob4";
    }

    @Override
    public String getDescription() {
        return "TestJob4 description";
    }

}
