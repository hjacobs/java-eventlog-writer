package de.zalando.zomcat.jobs.management.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import org.quartz.impl.StdSchedulerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.LocalTaskExecutorThreadPool;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.JobConfigSource;
import de.zalando.zomcat.jobs.management.JobManager;
import de.zalando.zomcat.jobs.management.JobManagerException;
import de.zalando.zomcat.jobs.management.JobManagerManagedJob;
import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;
import de.zalando.zomcat.jobs.management.JobSchedulingConfigurationProvider;
import de.zalando.zomcat.jobs.management.JobSchedulingConfigurationProviderException;
import de.zalando.zomcat.jobs.management.JobSchedulingConfigurationType;
import de.zalando.zomcat.util.DiscardingThreadPoolTaskExecutor;

/**
 * Default Implementation of {@link JobManager} interface. Simple component that manages Quartz Jobs. Features include:
 * on demand scheduling, on demand rescheduling, on demand job cancelation, maintanence mode support, job history incl
 * results etc.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public final class DefaultJobManager implements JobManager, JobListener, Runnable, ApplicationContextAware {

    /**
     * Logger for this class.
     */
    private static final transient Logger LOG = LoggerFactory.getLogger(DefaultJobManager.class);

    /**
     * Date Formatter Pattern.
     */
    private static final transient String DATE_FORMATTER_PATTERN = "yyyy-MM-dd HH:mm:SS";

    /**
     * Simple Quartz Listener name.
     */
    private static final transient String QUARTZ_JOB_LISTENER_NAME = "Z-JOB-MANAGER-QUARTZ-JOB-LISTENER";

    /**
     * Executor PoolSize JobDetailMap Property Name.
     */
    private static final transient String POOL_SIZE_JOB_DATA_KEY = "pool";

    /**
     * Executor QueueSize JobDetailMap Property Name.
     */
    private static final transient String QUEUE_CAPACITY_KEY = "queue";

    /**
     * Simple Map of Class<?> to AtomicInteger Sequences.
     */
    private final Map<Class<?>, AtomicInteger> jobNameSequence;

    /**
     * Map of simple Managed Jobs.
     */
    private final Map<JobSchedulingConfiguration, JobManagerManagedJob> managedJobs;

    /**
     * Maintancene Mode activation.
     */
    private final AtomicBoolean maintanenceModeActive = new AtomicBoolean(false);

    /**
     * Job Scheduling Config Provider.
     */
    private JobSchedulingConfigurationProvider delegatingJobSchedulingConfigProvider;

    /**
     * Treadpool Executor for {@link JobSchedulingConfiguration} polling.
     */
    private final ScheduledThreadPoolExecutor schedulingConfigPollingExecutor;

    /**
     * Spring {@link ApplicationContext} - this Bean is {@link ApplicationContextAware}.
     */
    private ApplicationContext applicationContext;

    @Autowired
    private JobConfigSource jobConfigSource;

    /**
     * Default Constructor.
     *
     * @throws  JobManagerException  if any error occurs during instantiation
     */
    public DefaultJobManager() throws JobManagerException {

        // schedulingContext.setInstanceId(instanceId);
        try {
            managedJobs = Maps.newConcurrentMap();
            jobNameSequence = Maps.newConcurrentMap();

            // quartzScheduler = new QuartzScheduler(resources, new
            // SchedulingContext(), 0, 0);
            schedulingConfigPollingExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
            schedulingConfigPollingExecutor.setThreadFactory(new DefaultJobManagerPollerThreadFactory());
        } catch (final SecurityException e) {
            throw new JobManagerException(e);
        } catch (final IllegalArgumentException e) {
            throw new JobManagerException(e);
        }

    }

    /**
     * Schedule a given Job.
     *
     * @param   jobSchedulingConfig  The Job Scheduling Configuration
     * @param   reschedule           Reschedule <code>true</code> or <code>false</code>
     *
     * @throws  JobManagerException  if any unanticipated error occurs during scheduling or rescheduling
     */
    private void scheduleOrRescheduleJob(final JobSchedulingConfiguration jobSchedulingConfig, final boolean reschedule)
        throws JobManagerException {
        validateJobSchedulingConfig(jobSchedulingConfig);

        try {

            // Create Job Detail from Scheduler Configuration
            final JobDetail jobDetail = toQuartzJobDetailFromJobSchedulingConfig(jobSchedulingConfig);
            final JobConfig jobConfig = jobSchedulingConfig.getJobConfig();

            // Create Quartz Trigger from Scheduler Configuration
            Trigger quartzTrigger = null;
            switch (jobSchedulingConfig.getJobType()) {

                case CRON :
                    quartzTrigger = new CronTriggerBean();
                    ((CronTriggerBean) quartzTrigger).setJobDetail(jobDetail);
                    ((CronTriggerBean) quartzTrigger).setCronExpression(jobSchedulingConfig.getCronExpression());
                    ((CronTriggerBean) quartzTrigger).setJobDataMap(jobDetail.getJobDataMap());
                    ((CronTriggerBean) quartzTrigger).setBeanName(String.format("%s%s", jobDetail.getName(),
                            "Trigger"));
                    quartzTrigger.setName(String.format("%s%s", jobDetail.getName(), "Trigger"));
                    quartzTrigger.setGroup(jobDetail.getGroup());
                    quartzTrigger.setJobName(jobDetail.getName());
                    quartzTrigger.setJobName(jobDetail.getGroup());
                    ((CronTriggerBean) quartzTrigger).afterPropertiesSet();

                    break;

                case SIMPLE :
                    quartzTrigger = new SimpleTriggerBean();
                    ((SimpleTriggerBean) quartzTrigger).setJobDetail(jobDetail);
                    ((SimpleTriggerBean) quartzTrigger).setRepeatInterval(jobSchedulingConfig.getIntervalMS());
                    ((SimpleTriggerBean) quartzTrigger).setStartDelay(jobSchedulingConfig.getStartDelayMS());
                    ((SimpleTriggerBean) quartzTrigger).setJobDataMap(jobDetail.getJobDataMap());
                    ((SimpleTriggerBean) quartzTrigger).setBeanName(String.format("%s%s", jobDetail.getName(),
                            "Trigger"));
                    quartzTrigger.setName(String.format("%s%s", jobDetail.getName(), "Trigger"));
                    quartzTrigger.setGroup(jobDetail.getGroup());
                    quartzTrigger.setJobName(jobDetail.getName());
                    quartzTrigger.setJobName(jobDetail.getGroup());
                    ((SimpleTriggerBean) quartzTrigger).afterPropertiesSet();
                    break;

                default :
                    throw new JobManagerException(String.format("Unsupported Job Scheduling Type: %s",
                            jobSchedulingConfig.getJobType()));

            }

            // Schedule the Job and return next RunDate
            if (reschedule) {
                cancelJob(jobSchedulingConfig);
            }

            // Default Executor Size - default avoids multiple Executions of
            // Same JOB
            int poolSize = 1;
            int queueCapacity = 0;

            if (jobSchedulingConfig.getJobData() != null) {
                if (jobSchedulingConfig.getJobData().containsKey(POOL_SIZE_JOB_DATA_KEY)) {
                    try {
                        poolSize = Integer.valueOf(jobSchedulingConfig.getJobData().get(POOL_SIZE_JOB_DATA_KEY));
                    } catch (final NumberFormatException nfe) {
                        throw new IllegalArgumentException("invalid thread pool size (not an integer)", nfe);
                    }
                }

                if (jobSchedulingConfig.getJobData().containsKey(QUEUE_CAPACITY_KEY)) {
                    try {
                        queueCapacity = Integer.valueOf(jobSchedulingConfig.getJobData().get(QUEUE_CAPACITY_KEY));
                    } catch (final NumberFormatException nfe) {
                        throw new IllegalArgumentException("invalid queue capacity size (not an integer)", nfe);
                    }
                }
            }

            final DiscardingThreadPoolTaskExecutor threadPool = new DiscardingThreadPoolTaskExecutor();
            threadPool.setCorePoolSize(poolSize);
            threadPool.setMaxPoolSize(poolSize);
            threadPool.setQueueCapacity(queueCapacity);
            threadPool.setBeanName(jobDetail.getName() + "Executor");
            threadPool.afterPropertiesSet();

            // Create SchedulerFactoryBean and associate with Trigger(s) and
            // Executor
            final Properties quartzProperties = new Properties();
            quartzProperties.setProperty(StdSchedulerFactory.PROP_THREAD_POOL_CLASS,
                LocalTaskExecutorThreadPool.class.getName());

            final SchedulerFactoryBean sfb = new SchedulerFactoryBean();
            sfb.setTaskExecutor(threadPool);
            sfb.setApplicationContextSchedulerContextKey("applicationContext");
            sfb.setTriggers(new Trigger[] {quartzTrigger});
            sfb.setExposeSchedulerInRepository(false);
            sfb.setBeanName(jobDetail.getName() + "Scheduler");
            sfb.setApplicationContext(applicationContext);
            sfb.setQuartzProperties(quartzProperties);

            // Put Scheduler into managed Map of Jobs
            managedJobs.put(jobSchedulingConfig,
                new DefaultJobManagerManagedJob(jobSchedulingConfig, jobDetail, quartzTrigger, sfb));

            // Check if a JobConfig could be found for Job
            if (jobConfig == null) {
                LOG.error(" No JobConfig found for Job. Skipping scheduling of Job: [{}].", jobSchedulingConfig);
                return;
            }

            // If Job is not Active - do not schedule
            if (jobConfig.getJobGroupConfig() != null && !jobConfig.getJobGroupConfig().isJobGroupActive()) {
                LOG.info("JobGroup: [{}] is not active. Skipping scheduling of Job: [{}].", jobSchedulingConfig,
                    jobConfig.getJobGroupConfig());
                return;
            }

            // If Job is not Active - do not schedule
            if (!jobConfig.isActive()) {
                LOG.info("Job is not active. Skipping scheduling of Job: [{}].", jobSchedulingConfig);
                return;
            }

            // If Job is not allowed on current AppInstanceKey - do not schedule
            if (!jobConfig.isAllowedAppInstanceKey(jobConfigSource.getAppInstanceKey())) {
                LOG.info("Skipping scheduling of Job: [{}]. Job is not allowed to run on AppInstance: [{}]. "
                        + "Allowed AppInstances are: {}",
                    new Object[] {
                        jobSchedulingConfig, jobConfigSource.getAppInstanceKey(), jobConfig.getAllowedAppInstanceKeys()
                    });
                return;
            }

            // Act as though the JobManager was Spring calling this Callback
            sfb.afterPropertiesSet();

            // Start the SchedulerFactoryBean
            sfb.start();

            // Maybe we need the next run date for the Status Page
            final Date nextRunTime = quartzTrigger.getNextFireTime();
            if (nextRunTime != null) {
                final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER_PATTERN);
                LOG.info("Scheduled Job: [{}]. Will run next at: [{}]", jobSchedulingConfig,
                    dateFormat.format(nextRunTime));
            } else {
                LOG.info("Scheduled Job: [{}].", jobSchedulingConfig);
            }
        } catch (final ParseException e) {
            throw new JobManagerException(e);
        } catch (final ClassNotFoundException e) {
            throw new JobManagerException(e);
        } catch (final Exception e) {
            throw new JobManagerException(e);
        }
    }

    /**
     * Validate given {@link JobSchedulingConfiguration}.
     *
     * @param   jobSchedulingConfig  the {@link JobSchedulingConfiguration} to validate
     *
     * @throws  IllegalArgumentException
     */
    private void validateJobSchedulingConfig(final JobSchedulingConfiguration jobSchedulingConfig)
        throws IllegalArgumentException {
        Preconditions.checkArgument(jobSchedulingConfig != null, "JobSchedulingConfig cannot be NULL");
        Preconditions.checkArgument(jobSchedulingConfig.getJobType() != null,
            "JobSchedulingConfig.JobType cannot be NULL");
        Preconditions.checkArgument(jobSchedulingConfig.getJobClass() != null,
            "JobSchedulingConfig.JobClass cannot be NULL");
        if (jobSchedulingConfig.getJobType() == JobSchedulingConfigurationType.CRON) {
            Preconditions.checkArgument(jobSchedulingConfig.getCronExpression() != null,
                "JobSchedulingConfig.cronExpression cannot be NULL on CRON Job");
        } else if (jobSchedulingConfig.getJobType() == JobSchedulingConfigurationType.SIMPLE) {
            Preconditions.checkArgument(jobSchedulingConfig.getStartDelayMS() != null,
                "JobSchedulingConfig.startDelayMS cannot be NULL on SIMPLE Job");
            Preconditions.checkArgument(jobSchedulingConfig.getIntervalMS() != null,
                "JobSchedulingConfig.intervalMS cannot be NULL on SIMPLE Job");
        }
    }

    private JobDetail toQuartzJobDetailFromJobSchedulingConfig(final JobSchedulingConfiguration config)
        throws ClassNotFoundException {
        final JobDetail retVal = new JobDetail(getJobName(config), config.getJobJavaClass());
        retVal.setJobDataMap(new JobDataMap(config.getJobData()));
        retVal.setDurability(false);

        // Set JobGroup if there is one
        if (config.getJobConfig() != null && config.getJobConfig().getJobGroupConfig() != null
                && config.getJobConfig().getJobGroupConfig().getJobGroupName() != null) {
            retVal.setGroup(config.getJobConfig().getJobGroupConfig().getJobGroupName());
        }

        return retVal;
    }

    /**
     * get job bean name from job class e.g. de.zalando.example.ExampleJob -> exampleJob if multiple instances of the
     * same job class are used we add an incrementing suffix, e.g. exampleJob, exampleJob1, exampleJob2, ..
     *
     * @param   jobSchedulingConfig  The {@link JobSchedulingConfiguration} of the Job
     *
     * @return
     *
     * @throws  ClassNotFoundException
     */
    private String getJobName(final JobSchedulingConfiguration jobSchedulingConfig) throws ClassNotFoundException {
        final Class<?> clazz = jobSchedulingConfig.getJobJavaClass();
        String name = jobSchedulingConfig.getJobName();

        Preconditions.checkArgument(name.endsWith("Job"), "job class name must end with 'Job': " + name);

        AtomicInteger seq = jobNameSequence.get(clazz);

        if (seq == null) {
            seq = new AtomicInteger(1);
        } else {
            seq.incrementAndGet();
        }

        name += seq.get();

        jobNameSequence.put(clazz, seq);

        return name;
    }

    /**
     * Get {@link JobManagerManagedJob} for given JobDetail.
     *
     * @param   jobDetail  The Quartz {@link JobDetail} to find {@link JobManagerManagedJob} for
     *
     * @return  The matching {@link JobManagerManagedJob} or <code>null</code> if no match could be found
     */
    private JobManagerManagedJob getManagedJobByJobDetail(final JobDetail jobDetail) {
        JobManagerManagedJob retVal = null;
        for (final JobManagerManagedJob curManagedJob : managedJobs.values()) {
            if (curManagedJob != null && curManagedJob.getQuartzJobDetail() != null
                    && jobDetail.equals(curManagedJob.getQuartzJobDetail())) {
                retVal = curManagedJob;
                break;
            }
        }

        return retVal;
    }

    /**
     * Get {@link JobManagerManagedJob} for given JobDetail.
     *
     * @param   jobDetail  The Quartz {@link JobDetail} to find {@link JobManagerManagedJob} for
     *
     * @return  The matching {@link JobManagerManagedJob} or <code>null</code> if no match could be found
     */
    private JobManagerManagedJob getManagedJobByTrigger(final Trigger jobTrigger) {
        JobManagerManagedJob retVal = null;
        for (final JobManagerManagedJob curManagedJob : managedJobs.values()) {
            if (curManagedJob != null && curManagedJob.getQuartzTrigger() != null
                    && jobTrigger.equals(curManagedJob.getQuartzTrigger())) {
                retVal = curManagedJob;
                break;
            }
        }

        return retVal;
    }

    /**
     * Get {@link JobManagerManagedJob} for given JobDetail Name and Group.
     *
     * @param   quartzJobDetailName
     * @param   quartzJobDetailGroup
     *
     * @return
     */
    private JobManagerManagedJob getManagedJobByJobDetailNameAndJobDetailGroup(final String quartzJobDetailName,
            final String quartzJobDetailGroup) {
        JobManagerManagedJob retVal = null;
        for (final JobManagerManagedJob curManagedJob : managedJobs.values()) {
            if (curManagedJob != null && curManagedJob.getQuartzJobDetail() != null
                    && curManagedJob.getQuartzJobDetail().getName().equals(quartzJobDetailName)
                    && curManagedJob.getQuartzJobDetail().getGroup().equals(quartzJobDetailGroup)) {
                retVal = curManagedJob;
                break;
            }
        }

        return retVal;
    }

    /**
     * This method reads {@link JobSchedulingConfiguration}s from {@link JobSchedulingConfigurationProvider} and
     * (re)schedules/cancels scheduled Jobs accordingly. Only a single thread must be allowed to to this at any given
     * time.
     *
     * @throws  JobManagerException
     */
    private synchronized void updateSchedulingConfigurationsAndRescheduleManagedJobs() throws JobManagerException {

        // Refresh Active Configurations via
        // DelegatingJobSchedulingConfigProvider
        try {

            // Since this List is only read from but not written to - there is
            // no need for thread safety
            // If alterations were to be made - replace this with
            // CopyOnWriteArrayList which is a thread safe variant
            // of the ArrayList and should be able to handle up to a thousand
            // elements with negligible performance
            // impact
            final List<JobSchedulingConfiguration> currentProvidedConfigs =
                delegatingJobSchedulingConfigProvider.provideSchedulerConfigs();

            LOG.info("Provider retrieved: [{}] SchedulingConfigs", currentProvidedConfigs.size());

            // Loop through provided Configurations and check if it is either not scheduled at all or if a reschedule
            // is required
            for (final JobSchedulingConfiguration curJobSchedulingConfig : currentProvidedConfigs) {

                // Check if the current Configuration is entirely new
                if (!this.managedJobs.containsKey(curJobSchedulingConfig)) {

                    // Schedule Job
                    this.scheduleJob(curJobSchedulingConfig);
                } else if (this.managedJobs.containsKey(curJobSchedulingConfig)) {

                    // Check if Configuration is altered
                    final JobManagerManagedJob managedJob = this.managedJobs.get(curJobSchedulingConfig);
                    if (!curJobSchedulingConfig.isEqual(managedJob.getJobSchedulingConfig())) {
                        this.rescheduleJob(curJobSchedulingConfig);
                    }
                }
            }

            // Loop through already scheduled Jobs and check if a configuration
            // was removed from provided
            // configuration List
            for (final JobSchedulingConfiguration curJobSchedulingConfig : managedJobs.keySet()) {
                if (!currentProvidedConfigs.contains(curJobSchedulingConfig)) {
                    cancelJob(curJobSchedulingConfig);
                }
            }

        } catch (final JobSchedulingConfigurationProviderException e) {
            throw new JobManagerException(String.format(
                    "Refresh of JobSchedulingConfiguration of Jobs failed with Error: [%s]", e.getMessage()), e);
        }
    }

    /**
     * Setter for Delegating JobSchedulingConfigProvider.
     *
     * @param  delegatingJobSchedulingConfigProvider  The Provider to set
     */
    public void setDelegatingJobSchedulingConfigProvider(
            final JobSchedulingConfigurationProvider delegatingJobSchedulingConfigProvider) {
        this.delegatingJobSchedulingConfigProvider = delegatingJobSchedulingConfigProvider;
    }

    /**
     * {@link JobManager} interface implementations.
     */
    @Override
    public List<JobManagerManagedJob> getManagedJobs() {
        return new ArrayList<JobManagerManagedJob>(managedJobs.values());
    }

    @Override
    public void scheduleJob(final JobSchedulingConfiguration jobSchedulingConfig) throws JobManagerException {
        scheduleOrRescheduleJob(jobSchedulingConfig, false);
    }

    @Override
    public void rescheduleJob(final JobSchedulingConfiguration jobSchedulingConfig) throws JobManagerException {
        scheduleOrRescheduleJob(jobSchedulingConfig, true);
    }

    @Override
    public void triggerJob(final JobSchedulingConfiguration jobSchedulingConfiguration) throws JobManagerException {
        try {
            Preconditions.checkArgument(jobSchedulingConfiguration != null,
                "Cannot trigger Job for NULL JobSchedulingConfiguration");

            final JobManagerManagedJob job = managedJobs.get(jobSchedulingConfiguration);
            Preconditions.checkArgument(job != null, "Could not find Managed Job for JobSchedulingConfiguration: [{}]",
                jobSchedulingConfiguration);
            job.getQuartzSchedulerFactoryBean().getScheduler().triggerJob(job.getQuartzJobDetail().getName(),
                job.getQuartzJobDetail().getGroup());
        } catch (final SchedulerException e) {
            throw new JobManagerException(e);
        } catch (final IllegalArgumentException e) {
            throw new JobManagerException(e);
        }

    }

    @Override
    public void triggerJob(final JobDetail quartzJobDetail) throws JobManagerException {
        final JobManagerManagedJob job = getManagedJobByJobDetail(quartzJobDetail);
        triggerJob(job.getJobSchedulingConfig());
    }

    @Override
    public void triggerJob(final Trigger quartzTrigger) throws JobManagerException {
        final JobManagerManagedJob job = getManagedJobByTrigger(quartzTrigger);
        triggerJob(job.getJobSchedulingConfig());
    }

    @Override
    public void triggerJob(final String quartzJobDetailName, final String quartzJobDetailGroup)
        throws JobManagerException {
        try {
            Preconditions.checkArgument(quartzJobDetailName != null, "Parameter quartJobDetailName must not be NULL");

            final JobManagerManagedJob job = getManagedJobByJobDetailNameAndJobDetailGroup(quartzJobDetailName,
                    quartzJobDetailGroup == null || quartzJobDetailGroup.trim().isEmpty() ? Scheduler.DEFAULT_GROUP
                                                                                          : quartzJobDetailGroup);
            if (job == null) {
                throw new JobManagerException(String.format(
                        "Could not find ManagedJob for JobName: [%s] and JobGroup: [%s]", quartzJobDetailName,
                        quartzJobDetailGroup));
            }

            triggerJob(job.getJobSchedulingConfig());
        } catch (final IllegalArgumentException e) {
            throw new JobManagerException(e);
        }
    }

    @Override
    public void cancelJob(final JobSchedulingConfiguration jobSchedulingConfig) throws JobManagerException {
        try {
            Preconditions.checkArgument(jobSchedulingConfig != null,
                "Cancel Job for NULL JobSchedulingConfiguration is not possible");

            final JobManagerManagedJob managedJob = managedJobs.get(jobSchedulingConfig);
            Preconditions.checkArgument(managedJob != null,
                "Could not find Managed Job for JobSchedulingConfiguration: [{}]", jobSchedulingConfig);
            LOG.info("Canceled Job: [{}]", managedJob);
            managedJob.getQuartzSchedulerFactoryBean().getScheduler().shutdown();
            LOG.debug("Stopped Job Scheduler for Job: [{}]", managedJob);
            managedJob.getQuartzSchedulerFactoryBean().stop();
            LOG.debug("Stopped Job SchedulerFactory for Job: [{}]", managedJob);
            managedJobs.remove(jobSchedulingConfig);
            LOG.debug("Removed Job from Map of managed jobs. Job: [{}]", managedJob);
        } catch (final SchedulerException e) {
            throw new JobManagerException(e);
        } catch (final IllegalArgumentException e) {
            throw new JobManagerException(e);
        }

    }

    @Override
    public void cancelAllJobs() throws JobManagerException {
        for (final JobSchedulingConfiguration curConfig : managedJobs.keySet()) {
            cancelJob(curConfig);
        }
    }

    @Override
    public void updateJobSchedulingConfigurations() throws JobManagerException {
        this.updateSchedulingConfigurationsAndRescheduleManagedJobs();
    }

    @Override
    public void startup() throws JobManagerException {
        LOG.info("Starting up JobManager");
        Preconditions.checkArgument(delegatingJobSchedulingConfigProvider != null,
            "DefaultJobManager has no DelegatingJobSchedulingConfigProvider set");

        // Do Initial Scheduling
        LOG.info("Fetching/Scheduling initial Scheduling Configuration...");
        this.updateSchedulingConfigurationsAndRescheduleManagedJobs();
        LOG.info("Starting Job Scheduling Configuration update Poller...");

        // Schedule the Rescheduling Thread
        schedulingConfigPollingExecutor.scheduleAtFixedRate(this, 1, 1, TimeUnit.MINUTES);
        LOG.info("Finished starting up JobManager");
    }

    @Override
    public void shutdown() throws JobManagerException {
        LOG.info("Shutting down JobManager...");
        schedulingConfigPollingExecutor.shutdown();
        LOG.info("Shut down DefaultJobManagers Configuration Poller/Job Rescheduler");
        cancelAllJobs();
        LOG.info("Shut down Quartz Schedulers");
        LOG.info("Finished shutting down JobManager");
    }

    @Override
    public boolean isMainanenceModeActive() {
        return this.maintanenceModeActive.get();
    }

    @Override
    public void setMainanenceModeActive(final boolean isMaintanenceMode) throws JobManagerException {
        this.maintanenceModeActive.set(isMaintanenceMode);
        if (!this.maintanenceModeActive.get()) {
            cancelAllJobs();
        } else {
            this.updateSchedulingConfigurationsAndRescheduleManagedJobs();
        }
    }

    /**
     * {@link JobListener} interface implementations The JobManager should be aware of Jobs being started, finished etc.
     */
    @Override
    public String getName() {
        return QUARTZ_JOB_LISTENER_NAME;
    }

    @Override
    public void jobExecutionVetoed(final JobExecutionContext context) {
        final JobManagerManagedJob currentJob = getManagedJobByJobDetail(context.getJobDetail());
        LOG.error("Job Execution was vetoed. Job: [{}]", currentJob.getJobSchedulingConfig());
    }

    @Override
    public void jobToBeExecuted(final JobExecutionContext context) {
        final JobManagerManagedJob currentJob = getManagedJobByJobDetail(context.getJobDetail());
        if (currentJob != null) {
// currentJob.jobStarted(new Date());
        } else {
            LOG.warn(
                "Could find JobManagerManagedJob entry for: [jobToBeExecuted] callback. JobExecutionContext was: [{}]",
                context);
        }
    }

    @Override
    public void jobWasExecuted(final JobExecutionContext context, final JobExecutionException jobException) {
        final JobManagerManagedJob currentJob = getManagedJobByJobDetail(context.getJobDetail());
        if (currentJob != null) {
// currentJob.jobStopped(new Date(), jobException);
        } else {
            LOG.warn(
                "Could find JobManagerManagedJob entry for: [jobWasExecuted] callback. JobExecutionContext was: [{}]. JobExecutionException was: [{}]",
                new Object[] {context, jobException.getMessage(), jobException});
        }
    }

    /**
     * {@link Runnable} interface implementation This method contains the refresh/rescheduling functionality.
     */
    @Override
    public void run() {
        try {
            updateSchedulingConfigurationsAndRescheduleManagedJobs();
        } catch (final JobManagerException e) {
            LOG.error("An error occured trying to update Schedulering Configurations/rescheduling Jobs. Error: [{}].",
                e.getMessage(), e);
        }
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
