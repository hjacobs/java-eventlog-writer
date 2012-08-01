package de.zalando.zomcat.jobs.management.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.JobDetail;
import org.quartz.Trigger;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.google.common.collect.Lists;

import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;

/**
 * Simple Managed Job for {@link DefaultJobManager}.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public class DefaultJobManagerManagedJob {

    private static final transient int MAX_HISTORY_COUNT = 10;

    private final JobSchedulingConfiguration jobSchedulingConfig;

    private final JobDetail quartzJobDetail;

    private final Trigger quartzTrigger;

    private final SchedulerFactoryBean schedulerFactoryBean;

    private final AtomicInteger executionCount;

    private final List<DefaultJobManagerManagedJobHistory> history;

    public DefaultJobManagerManagedJob(final JobSchedulingConfiguration jobSchedulingConfig,
            final JobDetail quartzJobDetail, final Trigger quartzTrigger,
            final SchedulerFactoryBean schedulerFactoryBean) {
        super();
        this.jobSchedulingConfig = jobSchedulingConfig;
        this.quartzJobDetail = quartzJobDetail;
        this.quartzTrigger = quartzTrigger;
        this.schedulerFactoryBean = schedulerFactoryBean;

        this.executionCount = new AtomicInteger(0);
        this.history = Lists.newCopyOnWriteArrayList();
    }

    /**
     * Getter for Field: jobSchedulingConfig
     *
     * @return  the jobSchedulingConfig
     */
    public JobSchedulingConfiguration getJobSchedulingConfig() {
        return jobSchedulingConfig;
    }

    /**
     * Getter for Field: quartzJobDetail
     *
     * @return  the quartzJobDetail
     */
    public JobDetail getQuartzJobDetail() {
        return quartzJobDetail;
    }

    /**
     * Get SchedulerFactoryBean.
     *
     * @return  The {@link SchedulerFactoryBean}
     */
    public SchedulerFactoryBean getSchedulerFactoryBean() {
        return schedulerFactoryBean;
    }

    /**
     * Returns next Fire Time of Jobs associated Trigger.
     *
     * @return
     */
    public Date getNextJobRunDate() {
        return quartzTrigger.getNextFireTime();
    }

    /**
     * Getter for Field: executionCount
     *
     * @return  the executionCount
     */
    public int getExecutionCount() {
        return executionCount.get();
    }

    /**
     * Create a JobHistory Entry with given StartDate.
     *
     * @param  startDate
     */
    public void jobStarted(final Date startDate) {

        // Remove last entry until only MAX_HISTORY_COUNT History entries remain per Job
        while (history.size() >= MAX_HISTORY_COUNT) {
            history.remove(history.size() - 1);
        }

        history.add(0, new DefaultJobManagerManagedJobHistory());
        history.get(0).jobStarted(startDate);
        executionCount.incrementAndGet();
    }

    /**
     * Finish JobHistory Entry with given StopDate.
     *
     * @param  stoppedDate
     * @param  exception
     */
    public void jobStopped(final Date stoppedDate, final Exception exception) {
        history.get(0).jobStopped(stoppedDate, exception);
    }

}
