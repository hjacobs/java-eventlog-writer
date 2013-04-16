package de.zalando.zomcat.jobs;

import org.quartz.JobExecutionContext;

public interface JobListener {

    void onExecutionSetUp(RunningWorker runningWorker, JobExecutionContext context, String appInstanceKey);

    void onExecutionTearDown(RunningWorker runningWorker);

    void startRunning(RunningWorker runningWorker, JobExecutionContext context, String appInstanceKey);

    void stopRunning(RunningWorker runningWorker, Throwable t);

}
