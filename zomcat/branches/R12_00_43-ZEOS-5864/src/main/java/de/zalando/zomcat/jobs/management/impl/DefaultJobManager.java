package de.zalando.zomcat.jobs.management.impl;

import java.util.Properties;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import org.quartz.impl.StdSchedulerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import de.zalando.zomcat.jobs.AbstractJob;
import de.zalando.zomcat.jobs.RunningWorker;
import de.zalando.zomcat.jobs.management.JobManager;
import de.zalando.zomcat.jobs.management.JobManagerException;
import de.zalando.zomcat.jobs.management.JobManagerManagedJob;
import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;
import de.zalando.zomcat.jobs.management.quartz.QuartzDiscardingThreadPoolTaskExecutor;
import de.zalando.zomcat.util.DiscardingThreadPoolTaskExecutor;

/**
 * Default Implementation of {@link JobManager} interface. Simple component that manages Quartz Jobs. Features include:
 * on demand scheduling, on demand rescheduling, on demand job cancelation, maintanence mode support, job history incl
 * results, per AppInstance job and job group (de)activation override, etc. Job Management occurs by reusing the Spring
 * Framework Quartz Integration classes. The DefaultJobManager acts as though it were the Spring Framework initializing
 * all necessary Job components as Spring would during its Context Startup.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public final class DefaultJobManager extends AbstractJobManager {

    /**
     * Logger for this class.
     */
    private static final transient Logger LOG = LoggerFactory.getLogger(DefaultJobManager.class);

    /**
     * Default Constructor.
     *
     * @throws  JobManagerException  if any error occurs during instantiation
     */
    public DefaultJobManager() throws JobManagerException {
        super();
    }

    @Override
    protected void onCancelJob(final JobManagerManagedJob managedJob, final boolean removeFromManagedJobs)
        throws JobManagerException {

        // Remove from Managed Job Map if Job is to be removed entirely
        if (removeFromManagedJobs) {

            // Check if there are Job Instances still running before Stopping the Jobs Bean Infrastructure
            LOG.debug("Stopped Job Scheduler for Job: [{}]", managedJob);
            ((DefaultJobManagerManagedJob) managedJob).getQuartzSchedulerFactoryBean().stop();
            LOG.debug("Stopped Job SchedulerFactory for Job: [{}]", managedJob);
            getManagedJobsInternal().remove(managedJob.getJobSchedulingConfig());
            LOG.debug("Removed Job from Map of managed jobs. Job: [{}]", managedJob);
        }
    }

    @Override
    protected JobManagerManagedJob onCreateManagedJob(final JobSchedulingConfiguration jobSchedulingConfiguration,
            final JobDetail jobDetail, final Trigger trigger, final int poolSize, final int queueSize)
        throws Exception {
        final DiscardingThreadPoolTaskExecutor threadPool = new DiscardingThreadPoolTaskExecutor();
        threadPool.setCorePoolSize(poolSize);
        threadPool.setMaxPoolSize(poolSize);
        threadPool.setQueueCapacity(queueSize);
        threadPool.setBeanName(jobDetail.getName() + "Executor");
        threadPool.afterPropertiesSet();

        // Create SchedulerFactoryBean and associate with Trigger(s) and
        // Executor
        final Properties quartzProperties = new Properties();
        quartzProperties.setProperty(StdSchedulerFactory.PROP_THREAD_POOL_CLASS,
            QuartzDiscardingThreadPoolTaskExecutor.class.getName());

        final SchedulerFactoryBean sfb = new SchedulerFactoryBean();
        sfb.setTaskExecutor(threadPool);
        sfb.setApplicationContextSchedulerContextKey("applicationContext");
        sfb.setExposeSchedulerInRepository(false);
        sfb.setBeanName(jobDetail.getName() + "Scheduler");
        sfb.setApplicationContext(getApplicationContext());
        sfb.setQuartzProperties(quartzProperties);
        sfb.setExposeSchedulerInRepository(true);

        // Act as though the JobManager was Spring calling this Callback
        sfb.afterPropertiesSet();

        // Add Global Job Listener
        sfb.getScheduler().addGlobalJobListener(this);

        // Start the SchedulerFactoryBean
        sfb.start();

        // Create Managed Job Instance for Job
        final DefaultJobManagerManagedJob managedJob = new DefaultJobManagerManagedJob(jobSchedulingConfiguration,
                jobDetail, trigger, sfb, poolSize);

        return managedJob;
    }

    /**
     * Check if a given Job is scheduled.
     *
     * @param   job  The {@link JobManagerManagedJob} to check
     *
     * @return  <code>true</code> if the job is scheduled, <code>false</code> otheriwsae
     *
     * @throws  SchedulerException  if the Quartz Scheduler has a problem retrieving the appropriate information
     */
    @Override
    protected boolean isJobScheduled(final JobManagerManagedJob job) throws SchedulerException {
        return job != null && job.getQuartzScheduler() != null && !job.getQuartzScheduler().isInStandbyMode()
                && job.getQuartzScheduler().getTrigger(job.getQuartzTrigger().getName(),
                    job.getQuartzTrigger().getGroup()) != null;
    }

    @Override
    public void jobToBeExecuted(final JobExecutionContext context) {
        final JobManagerManagedJob currentJob = getManagedJobByJobDetail(context.getJobDetail());

        if (currentJob != null) {
            final Job quartzJob = context.getJobInstance();
            if (quartzJob != null && AbstractJob.class.isInstance(quartzJob)) {
                ((AbstractJob) quartzJob).setJobConfig(currentJob.getJobSchedulingConfig().getJobConfig());
                try {
                    ((AbstractJob) quartzJob).setJobGroupConfig(getJobGroupConfigByJobGroupName(
                            currentJob.getJobSchedulingConfig().getJobConfig().getJobGroupName()));
                    ((AbstractJob) quartzJob).setApplicationContext(getApplicationContext());
                } catch (final JobManagerException e) {
                    LOG.error("Could not set JobGroupConfig on Job. Error was: [{}]", e.getMessage(), e);
                }
            }

            if (quartzJob != null && RunningWorker.class.isInstance(quartzJob)) {
                currentJob.onStartRunningWorker((RunningWorker) quartzJob);
            }
        } else {
            LOG.warn(
                "Could find JobManagerManagedJob entry for: [jobToBeExecuted] callback. JobExecutionContext was: [{}]",
                context);
        }
    }

}
