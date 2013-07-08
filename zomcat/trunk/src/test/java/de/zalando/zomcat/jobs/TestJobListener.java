package de.zalando.zomcat.jobs;

import org.quartz.JobExecutionContext;

public class TestJobListener implements JobListener {

    private int onExecutionSetUpCounter;
    private int onExecutionTearDownCounter;
    private int startRunningCounter;
    private int stopRunningCounter;

    @Override
    public void onExecutionSetUp(final RunningWorker runningWorker, final JobExecutionContext context,
            final String appInstanceKey) {
        onExecutionSetUpCounter++;

    }

    @Override
    public void onExecutionTearDown(final RunningWorker runningWorker) {
        onExecutionTearDownCounter++;

    }

    @Override
    public void startRunning(final RunningWorker runningWorker, final JobExecutionContext context,
            final String appInstanceKey) {
        startRunningCounter++;

    }

    @Override
    public void stopRunning(final RunningWorker runningWorker, final Throwable t) {
        stopRunningCounter++;
    }

    public int getOnExecutionSetUpCounter() {
        return onExecutionSetUpCounter;
    }

    public int getOnExecutionTearDownCounter() {
        return onExecutionTearDownCounter;
    }

    public int getStartRunningCounter() {
        return startRunningCounter;
    }

    public int getStopRunningCounter() {
        return stopRunningCounter;
    }

}
