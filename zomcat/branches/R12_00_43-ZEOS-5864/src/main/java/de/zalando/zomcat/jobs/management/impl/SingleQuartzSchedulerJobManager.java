package de.zalando.zomcat.jobs.management.impl;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

import org.quartz.impl.StdSchedulerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.zomcat.jobs.AbstractJob;
import de.zalando.zomcat.jobs.RunningWorker;
import de.zalando.zomcat.jobs.management.JobManager;
import de.zalando.zomcat.jobs.management.JobManagerException;
import de.zalando.zomcat.jobs.management.JobManagerManagedJob;
import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;

/**
 * Implementation of {@link JobManager} interface using only a single Quartz Scheduler and a single ThreadPool. Handling
 * Quartz Events correctly enables the Job manager to constrain Job Instances as defined by the Jobs Configuration
 * (POOL_SIZE, QUEUE_SIZE). This solution uses far less resources than the 'one scheduler per job' approach (single
 * Scheduler, single ThreadPool instead of per Job creation of Schedulers and Threadpools). Features include: on demand
 * scheduling, on demand rescheduling, on demand job cancelation, maintanence mode support, job history incl results,
 * per AppInstance job and job group (de)activation override, etc. In order to use this {@link JobManager} correctly,
 * the {@link AbstractJob} must set its own {@link Thread} name when running according to data provided by the JobData
 * map. The Jobs {@link Thread}s name can be located in the JobData map, but must be set on the respective current
 * {@link Thread} by the Job itself. The {@link AbstractJob} should be able to do this
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public final class SingleQuartzSchedulerJobManager extends AbstractJobManager implements TriggerListener {

    /**
     * Logger for this class.
     */
    private static final transient Logger LOG = LoggerFactory.getLogger(SingleQuartzSchedulerJobManager.class);

    /**
     * Quartz Scheduler to use.
     */
    private Scheduler quartzScheduler;

    /**
     * Default Constructor.
     *
     * @throws  JobManagerException  if any error occurs during instantiation
     */
    public SingleQuartzSchedulerJobManager() throws JobManagerException {
        super();
    }

    @Override
    protected void onStartup() throws JobManagerException {
        super.onStartup();
        try {
            this.quartzScheduler = StdSchedulerFactory.getDefaultScheduler();
            this.quartzScheduler.addGlobalJobListener(this);
            this.quartzScheduler.addGlobalTriggerListener(this);
            this.quartzScheduler.start();
        } catch (final SchedulerException e) {
            throw new JobManagerException(e);
        }
    }

    @Override
    protected void onShutdown() throws JobManagerException {
        super.onShutdown();
        try {
            if (quartzScheduler != null && quartzScheduler.isStarted()) {
                quartzScheduler.shutdown();
                quartzScheduler = null;
            }
        } catch (final SchedulerException e) {
            throw new JobManagerException(e);
        }
    }

    @Override
    protected void onCancelJob(final JobManagerManagedJob managedJob, final boolean removeFromManagedJobs)
        throws JobManagerException {
        try {
            managedJob.getQuartzScheduler().deleteJob(managedJob.getQuartzJobDetail().getName(),
                managedJob.getQuartzJobDetail().getGroup());
            if (removeFromManagedJobs) {

                // Check if there are Job Instances still running before Stopping the Jobs Bean Infrastructure
                LOG.debug("Stopped Job Scheduler for Job: [{}]", managedJob);

                getManagedJobsInternal().remove(managedJob.getJobSchedulingConfig());
                LOG.debug("Removed Job from Map of managed jobs. Job: [{}]", managedJob);
            }
        } catch (final SchedulerException e) {
            throw new JobManagerException(e);
        }
    }

    @Override
    protected JobManagerManagedJob onCreateManagedJob(final JobSchedulingConfiguration jobSchedulingConfiguration,
            final JobDetail jobDetail, final Trigger trigger, final int poolSize, final int queueSize)
        throws Exception {

        final SingleQuartzSchedulerJobManagerManagedJob managedJob = new SingleQuartzSchedulerJobManagerManagedJob(
                jobSchedulingConfiguration, jobDetail, trigger, quartzScheduler, poolSize);

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

    /**
     * {@link JobListener} interface implementations The JobManager should be aware of Jobs being started, finished etc.
     */
    @Override
    public void jobToBeExecuted(final JobExecutionContext context) { }

    /**
     * {@link TriggerListener} interface implementations The JobManager should be aware of Jobs being started, finished
     * etc.
     */
    @Override
    public void triggerComplete(final Trigger trigger, final JobExecutionContext context,
            final int triggerInstructionCode) { }

    @Override
    public void triggerFired(final Trigger trigger, final JobExecutionContext context) { }

    @Override
    public void triggerMisfired(final Trigger trigger) {
        LOG.error("Trigger misfired. Trigger was: [{}]", trigger);
    }

    @Override
    public boolean vetoJobExecution(final Trigger trigger, final JobExecutionContext context) {
        final JobManagerManagedJob currentJob = getManagedJobByJobDetail(context.getJobDetail());
        boolean retVal = false;
        if (currentJob != null) {
            if (currentJob.getRunningWorkerCount() < currentJob.getMaxConcurrentExecutionCount()) {
                Thread.currentThread().setName(currentJob.getQuartzJobDetail().getName() + "Executor");

                final Job quartzJob = context.getJobInstance();
                if (quartzJob != null && RunningWorker.class.isInstance(quartzJob)) {
                    currentJob.onStartRunningWorker((RunningWorker) quartzJob);
                }

                if (quartzJob != null && AbstractJob.class.isInstance(quartzJob)) {
                    ((AbstractJob) quartzJob).setJobConfig(currentJob.getJobSchedulingConfig().getJobConfig());
                    ((AbstractJob) quartzJob).setApplicationContext(getApplicationContext());
                    try {
                        ((AbstractJob) quartzJob).setJobGroupConfig(getJobGroupConfigByJobGroupName(
                                currentJob.getJobSchedulingConfig().getJobConfig().getJobGroupName()));
                    } catch (final JobManagerException e) {
                        LOG.error("Could not set JobGroupConfig on Job. Error was: [{}]", e.getMessage(), e);
                    }
                }

            } else {
                LOG.info("Job: [{}] execution vetoed - already running [{}/{}] Job Instances",
                    new Object[] {
                        currentJob.getJobSchedulingConfig(), currentJob.getRunningWorkerCount(),
                        currentJob.getMaxConcurrentExecutionCount()
                    });
                retVal = true;
            }
        } else {
            LOG.warn(
                "Could find JobManagerManagedJob entry for: [jobToBeExecuted] callback. JobExecutionContext was: [{}]",
                context);
        }

        return retVal;
    }
}
