package de.zalando.zomcat.jobs.management.impl;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;

/**
 * Simple Managed Job for {@link DefaultJobManager}.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public class DefaultJobManagerManagedJob extends AbstractJobManagerManagedJob {

    private final SchedulerFactoryBean schedulerFactoryBean;

    public DefaultJobManagerManagedJob(final JobSchedulingConfiguration jobSchedulingConfig,
            final JobDetail quartzJobDetail, final Trigger quartzTrigger,
            final SchedulerFactoryBean schedulerFactoryBean, final int maxConcurrentCount) {
        super(jobSchedulingConfig, quartzJobDetail, quartzTrigger, maxConcurrentCount);
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

    @Override
    public Scheduler getQuartzScheduler() {
        return schedulerFactoryBean.getScheduler();
    }

    public SchedulerFactoryBean getQuartzSchedulerFactoryBean() {
        return schedulerFactoryBean;
    }

}
