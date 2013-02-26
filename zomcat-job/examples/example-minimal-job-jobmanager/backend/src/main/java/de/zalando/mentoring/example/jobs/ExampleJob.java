package de.zalando.mentoring.example.jobs;

import org.quartz.JobExecutionContext;

import de.zalando.zomcat.jobs.AbstractJob;
import de.zalando.zomcat.jobs.JobConfig;

/**
 * @author  danieldelhoyo Date: 2/22/13 Time: 4:24 PM
 */
public class ExampleJob extends AbstractJob {
    @Override
    public void doRun(final JobExecutionContext jobExecutionContext, final JobConfig jobConfig) throws Exception {
        System.out.println("===> Hello world example job <===");

    }

    @Override
    public String getDescription() {
        return "Example of a Hello world job";
    }
}
