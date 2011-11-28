package de.zalando.zomcat.jobs;

import org.quartz.JobExecutionContext;

public interface JobListener {

    void startRunning(RunningWorker runningWorker, JobExecutionContext context, String host);

    void stopRunning(RunningWorker runningWorker, Throwable t);

}
