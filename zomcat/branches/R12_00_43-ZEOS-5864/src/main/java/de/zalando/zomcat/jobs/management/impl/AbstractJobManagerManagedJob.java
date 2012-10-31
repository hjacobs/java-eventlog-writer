package de.zalando.zomcat.jobs.management.impl;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.JobDetail;
import org.quartz.Trigger;

import de.zalando.zomcat.jobs.FinishedWorkerBean;
import de.zalando.zomcat.jobs.RunningWorker;
import de.zalando.zomcat.jobs.management.JobManagerManagedJob;
import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;
import de.zalando.zomcat.util.LinkedBoundedQueue;

/**
 * Simple Managed Job for {@link DefaultJobManager}.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public abstract class AbstractJobManagerManagedJob implements JobManagerManagedJob {

    private final JobSchedulingConfiguration jobSchedulingConfig;

    private final JobDetail quartzJobDetail;

    private final Trigger quartzTrigger;

    private final AtomicInteger executionCount;

    private final AtomicInteger runningWorkerCount;

    private final int maxConcurrentExecutionCount;

    private final LinkedBoundedQueue<RunningWorker> runningWorkerHistory;

    public AbstractJobManagerManagedJob(final JobSchedulingConfiguration jobSchedulingConfig,
            final JobDetail quartzJobDetail, final Trigger quartzTrigger, final int maxExecutionCount) {
        super();
        this.jobSchedulingConfig = jobSchedulingConfig;
        this.quartzJobDetail = quartzJobDetail;
        this.quartzTrigger = quartzTrigger;
        this.executionCount = new AtomicInteger(0);
        this.maxConcurrentExecutionCount = maxExecutionCount;
        this.runningWorkerCount = new AtomicInteger(0);
        this.runningWorkerHistory = new LinkedBoundedQueue<RunningWorker>(50);
    }

    @Override
    public final JobSchedulingConfiguration getJobSchedulingConfig() {
        return jobSchedulingConfig;
    }

    @Override
    public final JobDetail getQuartzJobDetail() {
        return quartzJobDetail;
    }

    @Override
    public final Trigger getQuartzTrigger() {
        return quartzTrigger;
    }

    @Override
    public final int getMaxConcurrentExecutionCount() {
        return maxConcurrentExecutionCount;
    }

    @Override
    public final Date getNextJobRunDate() {
        return quartzTrigger.getNextFireTime();
    }

    @Override
    public final int getExecutionCount() {
        return executionCount.get();
    }

    @Override
    public final void onFinishRunningWorker(final RunningWorker runningWorker) {
        this.runningWorkerHistory.add(new FinishedWorkerBean(runningWorker, this.jobSchedulingConfig.getJobConfig()));
        executionCount.incrementAndGet();
        runningWorkerCount.decrementAndGet();
    }

    @Override
    public final void onStartRunningWorker(final RunningWorker runningWorker) {
        runningWorkerCount.incrementAndGet();
    }

    @Override
    public final int getRunningWorkerCount() {
        return runningWorkerCount.intValue();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(this.getClass().getSimpleName());
        builder.append(" [jobSchedulingConfig=");
        builder.append(jobSchedulingConfig);
        builder.append(", maxConcurrentExecutionCount=");
        builder.append(maxConcurrentExecutionCount);
        builder.append("]");
        return builder.toString();
    }
}
