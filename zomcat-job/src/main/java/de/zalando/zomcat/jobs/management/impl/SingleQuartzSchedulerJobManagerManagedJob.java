package de.zalando.zomcat.jobs.management.impl;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;

/**
 * Simple Managed Job for {@link DefaultJobManager}.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public class SingleQuartzSchedulerJobManagerManagedJob extends AbstractJobManagerManagedJob {

    private final Scheduler quartzScheduler;

    public SingleQuartzSchedulerJobManagerManagedJob(final JobSchedulingConfiguration jobSchedulingConfig,
            final JobDetail quartzJobDetail, final Trigger quartzTrigger, final Scheduler quartzScheduler,
            final int maxConcurrentExecutionCount) {
        super(jobSchedulingConfig, quartzJobDetail, quartzTrigger, maxConcurrentExecutionCount);
        this.quartzScheduler = quartzScheduler;
    }

    @Override
    public Scheduler getQuartzScheduler() {
        return quartzScheduler;
    }

}
