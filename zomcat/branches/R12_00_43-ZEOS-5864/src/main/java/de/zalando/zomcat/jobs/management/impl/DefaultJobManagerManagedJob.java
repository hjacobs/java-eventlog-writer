package de.zalando.zomcat.jobs.management.impl;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.JobDetail;
import org.quartz.Trigger;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;

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
public class DefaultJobManagerManagedJob implements JobManagerManagedJob {

    private final JobSchedulingConfiguration jobSchedulingConfig;

    private final JobDetail quartzJobDetail;

    private final Trigger quartzTrigger;

    private final AtomicInteger executionCount;

    private final AtomicInteger runningWorkerCount;

    private final SchedulerFactoryBean schedulerFactoryBean;

    private final LinkedBoundedQueue<RunningWorker> runningWorkerHistory;

    public DefaultJobManagerManagedJob(final JobSchedulingConfiguration jobSchedulingConfig,
            final JobDetail quartzJobDetail, final Trigger quartzTrigger,
            final SchedulerFactoryBean schedulerFactoryBean) {
        super();
        this.jobSchedulingConfig = jobSchedulingConfig;
        this.quartzJobDetail = quartzJobDetail;
        this.quartzTrigger = quartzTrigger;
        this.schedulerFactoryBean = schedulerFactoryBean;
        this.executionCount = new AtomicInteger(0);
        this.runningWorkerCount = new AtomicInteger(0);
        this.runningWorkerHistory = new LinkedBoundedQueue<RunningWorker>(50);
    }

    /* (non-Javadoc)
     * @see de.zalando.zomcat.jobs.management.impl.Test#getJobSchedulingConfig()
     */
    @Override
    public JobSchedulingConfiguration getJobSchedulingConfig() {
        return jobSchedulingConfig;
    }

    /* (non-Javadoc)
     * @see de.zalando.zomcat.jobs.management.impl.Test#getQuartzJobDetail()
     */
    @Override
    public JobDetail getQuartzJobDetail() {
        return quartzJobDetail;
    }

    /* (non-Javadoc)
     * @see de.zalando.zomcat.jobs.management.impl.Test#getQuartzTrigger()
     */
    @Override
    public Trigger getQuartzTrigger() {
        return quartzTrigger;
    }

    /* (non-Javadoc)
     * @see de.zalando.zomcat.jobs.management.impl.Test#getQuartzSchedulerFactoryBean()
     */
    @Override
    public SchedulerFactoryBean getQuartzSchedulerFactoryBean() {
        return schedulerFactoryBean;
    }

    /* (non-Javadoc)
     * @see de.zalando.zomcat.jobs.management.impl.Test#getNextJobRunDate()
     */
    @Override
    public Date getNextJobRunDate() {
        return quartzTrigger.getNextFireTime();
    }

    /* (non-Javadoc)
     * @see de.zalando.zomcat.jobs.management.impl.Test#getExecutionCount()
     */
    @Override
    public int getExecutionCount() {
        return executionCount.get();
    }

    @Override
    public void onFinishRunningWorker(final RunningWorker runningWorker) {
        this.runningWorkerHistory.add(new FinishedWorkerBean(runningWorker, this.jobSchedulingConfig.getJobConfig()));
        executionCount.incrementAndGet();
        runningWorkerCount.decrementAndGet();
    }

    @Override
    public void onStartRunningWorker(final RunningWorker runningWorker) {
        runningWorkerCount.incrementAndGet();
    }

    @Override
    public int getRunningWorkerCount() {
        return runningWorkerCount.intValue();
    }

}
