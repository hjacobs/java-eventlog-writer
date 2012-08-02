package de.zalando.zomcat.jobs.management.impl;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.JobDetail;
import org.quartz.Trigger;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import de.zalando.zomcat.jobs.management.JobManagerManagedJob;
import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;

/**
 * Simple Managed Job for {@link DefaultJobManager}.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public class DefaultJobManagerManagedJob implements JobManagerManagedJob {

// private static final transient int MAX_HISTORY_COUNT = 10;

    private final JobSchedulingConfiguration jobSchedulingConfig;

    private final JobDetail quartzJobDetail;

    private final Trigger quartzTrigger;

    private final SchedulerFactoryBean schedulerFactoryBean;

    private final AtomicInteger executionCount;

// private final List<DefaultJobManagerManagedJobHistory> history;

    public DefaultJobManagerManagedJob(final JobSchedulingConfiguration jobSchedulingConfig,
            final JobDetail quartzJobDetail, final Trigger quartzTrigger,
            final SchedulerFactoryBean schedulerFactoryBean) {
        super();
        this.jobSchedulingConfig = jobSchedulingConfig;
        this.quartzJobDetail = quartzJobDetail;
        this.quartzTrigger = quartzTrigger;
        this.schedulerFactoryBean = schedulerFactoryBean;

        this.executionCount = new AtomicInteger(0);
// this.history = Lists.newCopyOnWriteArrayList();
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

// /**
// * Create a JobHistory Entry with given StartDate.
// *
// * @param  startDate
// */
// public void jobStarted(final Date startDate) {
//
// // Remove last entry until only MAX_HISTORY_COUNT History entries remain per Job
// while (history.size() >= MAX_HISTORY_COUNT) {
// history.remove(history.size() - 1);
// }
//
// history.add(0, new DefaultJobManagerManagedJobHistory());
// history.get(0).jobStarted(startDate);
// executionCount.incrementAndGet();
// }
//
// /**
// * Finish JobHistory Entry with given StopDate.
// *
// * @param  stoppedDate
// * @param  exception
// */
// public void jobStopped(final Date stoppedDate, final Exception exception) {
// history.get(0).jobStopped(stoppedDate, exception);
// }

}
