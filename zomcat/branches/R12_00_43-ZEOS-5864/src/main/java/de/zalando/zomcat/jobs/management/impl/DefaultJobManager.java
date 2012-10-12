package de.zalando.zomcat.jobs.management.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.Job;
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
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.zalando.zomcat.OperationMode;
import de.zalando.zomcat.jobs.AbstractJob;
import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.JobConfigSource;
import de.zalando.zomcat.jobs.JobGroupConfig;
import de.zalando.zomcat.jobs.RunningWorker;
import de.zalando.zomcat.jobs.management.JobManager;
import de.zalando.zomcat.jobs.management.JobManagerException;
import de.zalando.zomcat.jobs.management.JobManagerManagedJob;
import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;
import de.zalando.zomcat.jobs.management.JobSchedulingConfigurationProvider;
import de.zalando.zomcat.jobs.management.JobSchedulingConfigurationProviderException;
import de.zalando.zomcat.jobs.management.JobSchedulingConfigurationType;
import de.zalando.zomcat.jobs.management.quartz.QuartzDiscardingThreadPoolTaskExecutor;
import de.zalando.zomcat.util.DiscardingThreadPoolTaskExecutor;

/**
 * Default Implementation of {@link JobManager} interface. Simple component that manages Quartz Jobs. Features include:
 * on demand scheduling, on demand rescheduling, on demand job cancelation, maintanence mode support, job history incl
 * results, per AppInstance job and job group (de)activation override, etc.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public final class DefaultJobManager implements JobManager, JobListener, Runnable, ApplicationContextAware {

    /**
     * Logger for this class.
     */
    private static final transient Logger LOG = LoggerFactory.getLogger(DefaultJobManager.class);

    /**
     * Date Formatter Pattern used in Logging (Next Trigger Fire Date).
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
     * Map of simple Managed Jobs.
     */
    private final Set<JobGroupConfig> managedJobGroups;

    /**
     * Map of Application Instance specific JobGroupConfig overrides.
     */
    private final Map<JobSchedulingConfiguration, Boolean> instanceJobConfigOverrides;

    /**
     * List of Application Instance specific JobGroupConfig overrides.
     */
    private final Map<String, Boolean> instanceJobGroupConfigOverrides;

    /**
     * Maintancene Mode activation.
     */
    private OperationMode operationMode = OperationMode.NORMAL;

    /**
     * Job Scheduling Config Provider.
     */
    private JobSchedulingConfigurationProvider delegatingJobSchedulingConfigProvider;

    /**
     * Treadpool Executor for {@link JobSchedulingConfiguration} polling.
     */
    private ScheduledThreadPoolExecutor schedulingConfigPollingExecutor;

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
            managedJobGroups = Sets.newCopyOnWriteArraySet();
            jobNameSequence = Maps.newConcurrentMap();
            instanceJobConfigOverrides = Maps.newConcurrentMap();
            instanceJobGroupConfigOverrides = Maps.newConcurrentMap();
        } catch (final SecurityException e) {
            throw new JobManagerException(e);
        } catch (final IllegalArgumentException e) {
            throw new JobManagerException(e);
        }

    }

    /**
     * Create a JobSchedulingConfigurationPoller {@link Executor} that polls the {@link JobSchedulingConfiguration}s
     * periodically (every 5 minutes).
     *
     * @throws  JobManagerException  if any error occurs during creation of {@link Executor}
     */
    private void createJobSchedulingConfigurationPollerExecutor() throws JobManagerException {
        if (schedulingConfigPollingExecutor != null) {
            throw new JobManagerException(
                "Cannot start JobSchedulingConfigurationPollerExecutor - another Executor already exists");
        }

        schedulingConfigPollingExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
        schedulingConfigPollingExecutor.setThreadFactory(new DefaultJobManagerPollerThreadFactory());
        schedulingConfigPollingExecutor.scheduleAtFixedRate(this, 0, 1, TimeUnit.MINUTES);
    }

    /**
     * Cancel the JobSchedulingConfiguration Poller Thread Executor.
     *
     * @throws  JobManagerException  if the JobSchedulingConfiguration Poller Thread Executor is null and therefore
     *                               cannot be canceled
     */
    private void cancelJobSchedulingConfigurationPollerExecutor() throws JobManagerException {
        if (schedulingConfigPollingExecutor == null) {
            throw new JobManagerException(
                "Cannot cancel JobSchedulingConfigurationPollerExecutor - no Executor exists");
        }

        schedulingConfigPollingExecutor.shutdown();
        schedulingConfigPollingExecutor = null;
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

            // If the Job is not managed jet - create all necessary beans and add it to list of managed jobs
            if (!managedJobs.containsKey(jobSchedulingConfig)) {

                // Put Scheduler into managed Map of Jobs
                managedJobs.put(jobSchedulingConfig, createManagedJob(jobSchedulingConfig));
            }

            // Get current Managed Job - must be managed at this point
            final JobManagerManagedJob managedJob = managedJobs.get(jobSchedulingConfig);

            // Schedule the Job and return next RunDate
            if (reschedule && isJobScheduled(managedJob)) {
                cancelJob(jobSchedulingConfig, false);
            }

            // if a Job Config override exists with the same active state as the job itself - remove the override
            final boolean isJobActive = isJobActive(jobSchedulingConfig, null, null);
            if (this.instanceJobConfigOverrides.containsKey(jobSchedulingConfig)
                    && this.instanceJobConfigOverrides.get(jobSchedulingConfig) == isJobActive) {
                instanceJobConfigOverrides.remove(jobSchedulingConfig);
            }

            // if the Job is Active - schedule it, otherwise log WHY job is inactive
            if (isJobActive(jobSchedulingConfig, instanceJobConfigOverrides.get(jobSchedulingConfig),
                        instanceJobGroupConfigOverrides.get(jobSchedulingConfig.getJobConfig().getJobGroupName()))) {

                // Schedule the Jobs Trigger
                managedJob.getQuartzSchedulerFactoryBean().getScheduler().scheduleJob(managedJob.getQuartzJobDetail(),
                    managedJob.getQuartzTrigger());

                // Maybe we need the next run date for the Status Page
                final Date nextRunTime = managedJob.getQuartzTrigger().getNextFireTime();
                if (nextRunTime != null) {
                    final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMATTER_PATTERN);
                    LOG.info("Scheduled Job: [{}]. Will run next at: [{}]", jobSchedulingConfig,
                        dateFormat.format(nextRunTime));
                } else {
                    LOG.info("Scheduled Job: [{}].", jobSchedulingConfig);
                }
            } else {

                // Log Job Scheduling - Inactive Reasons
                logJobSchedulingConfigActiveState(jobSchedulingConfig,
                    instanceJobConfigOverrides.get(jobSchedulingConfig),
                    instanceJobGroupConfigOverrides.get(jobSchedulingConfig.getJobConfig().getJobGroupName()));
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
     * Create a Managed Job Instance for given {@link JobSchedulingConfiguration} instance.
     *
     * @param   jobSchedulingConfig  The {@link JobSchedulingConfiguration} instance to use for ManagedJobCreation
     *
     * @throws  ClassNotFoundException
     * @throws  Exception
     */
    private JobManagerManagedJob createManagedJob(final JobSchedulingConfiguration jobSchedulingConfig)
        throws ClassNotFoundException, Exception {

        // Create Job Detail from Scheduler Configuration
        final JobDetail jobDetail = toQuartzJobDetailFromJobSchedulingConfig(jobSchedulingConfig);

        // Create Quartz Trigger
        final Trigger quartzTrigger = createQuartzTrigger(jobSchedulingConfig, jobDetail);

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
            QuartzDiscardingThreadPoolTaskExecutor.class.getName());

        final SchedulerFactoryBean sfb = new SchedulerFactoryBean();
        sfb.setTaskExecutor(threadPool);
        sfb.setApplicationContextSchedulerContextKey("applicationContext");
        sfb.setExposeSchedulerInRepository(false);
        sfb.setBeanName(jobDetail.getName() + "Scheduler");
        sfb.setApplicationContext(applicationContext);
        sfb.setQuartzProperties(quartzProperties);
        sfb.setExposeSchedulerInRepository(true);

        // Act as though the JobManager was Spring calling this Callback
        sfb.afterPropertiesSet();

        // Add Global Job Listener
        sfb.getScheduler().addGlobalJobListener(this);

        // Start the SchedulerFactoryBean
        sfb.start();

        // Create Managed Job Instance for Job
        final JobManagerManagedJob managedJob = new DefaultJobManagerManagedJob(jobSchedulingConfig, jobDetail,
                quartzTrigger, sfb);

        return managedJob;
    }

    /**
     * Create a Quartz Trigger from {@link JobSchedulingConfiguration} and Quart {@link JobDetail} instances.
     *
     * @param   jobSchedulingConfig  The {@link JobSchedulingConfiguration} instance of the Job
     * @param   jobDetail            The Quartz {@link JobDetail} instance of the Job
     *
     * @return  The {@link Trigger} instance representing the scheduling information part of Jobs
     *          {@link JobSchedulingConfiguration}
     *
     * @throws  Exception  any other unanticipated error occurs
     */
    private Trigger createQuartzTrigger(final JobSchedulingConfiguration jobSchedulingConfig, final JobDetail jobDetail)
        throws Exception {

        // Create Quartz Trigger from Scheduler Configuration
        Trigger quartzTrigger = null;
        switch (jobSchedulingConfig.getJobType()) {

            case CRON :
                quartzTrigger = new CronTriggerBean();
                ((CronTriggerBean) quartzTrigger).setJobDetail(jobDetail);
                ((CronTriggerBean) quartzTrigger).setCronExpression(jobSchedulingConfig.getCronExpression());
                ((CronTriggerBean) quartzTrigger).setJobDataMap(jobDetail.getJobDataMap());
                ((CronTriggerBean) quartzTrigger).setBeanName(String.format("%s%s", jobDetail.getName(), "Trigger"));
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
                ((SimpleTriggerBean) quartzTrigger).setBeanName(String.format("%s%s", jobDetail.getName(), "Trigger"));
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

        return quartzTrigger;
    }

    /**
     * Check Active State of Job - used in Scheduling.
     *
     * @param   jobSchedulingConfig  The {@link JobSchedulingConfiguration} to check active state for
     *
     * @return  <code>true</code> if Job is active, <code>false</code> otherwise
     *
     * @throws  JobManagerException  if an unanticipated error occurs
     */
    private boolean isJobActive(final JobSchedulingConfiguration jobSchedulingConfig,
            final Boolean overrideConfigActive, final Boolean overrideGroupConfigActive) throws JobManagerException {

        // Check validity of JobSchedulingConfiguration
        if (jobSchedulingConfig == null) {
            throw new JobManagerException("Cannot accertain Job Active State from JobSchedulingConfiguration: [null]");
        }

        boolean retVal = true;
        final JobConfig jobConfig = jobSchedulingConfig.getJobConfig();

        // Check Group active State
        retVal = isJobGroupActive(jobConfig.getJobGroupName(), overrideGroupConfigActive);

        // If Job is not Active - and no Override exists - Job is not active
        if (!jobConfig.isActive() && overrideConfigActive == null) {
            retVal = false;
        }

        // If Job is not Active - Job is not active
        if (overrideConfigActive != null && !overrideConfigActive) {
            retVal = false;
        }

        // If Job is not allowed on current AppInstanceKey - Job is not active
        if (!jobConfig.isAllowedAppInstanceKey(jobConfigSource.getAppInstanceKey())) {
            retVal = false;
        }

        // if MaintanenceModes is active - no job is allowed to run
        if (isMainanenceModeActive()) {
            retVal = false;
        }

        return retVal;
    }

    /**
     * Find a managed {@link JobGroupConfig} by its name.
     *
     * @param   jobGroupName  The JobGroupName
     *
     * @return  The matching {@link JobGroupConfig} if there is one, <code>null</code> otherwise.
     *
     * @throws  JobManagerException  if the Parameter jobGroupName is <code>null</code>
     */
    private JobGroupConfig getJobGroupConfigByJobGroupName(final String jobGroupName) throws JobManagerException {
        JobGroupConfig retVal = null;
        if (Strings.isNullOrEmpty(jobGroupName)) {
            throw new JobManagerException("Cannot lookup JobGroup for JobGroup name: [null]");
        }

        for (final JobGroupConfig curJobGroupConfig : managedJobGroups) {
            if (jobGroupName.equals(curJobGroupConfig.getJobGroupName())) {
                retVal = curJobGroupConfig;
                break;
            }
        }

        return retVal;
    }

    /**
     * Check Active State of JobGroup.
     *
     * @param   jobSchedulingConfig        The {@link JobSchedulingConfiguration} to check active state for
     * @param   overrideGroupConfigActive  Local group active state override.
     *
     * @return  <code>true</code> if JobGroup is active, <code>false</code> otherwise
     *
     * @throws  JobManagerException  if an unanticipated error occurs
     */
    private boolean isJobGroupActive(final String jobGroupName, final Boolean overrideGroupConfigActive)
        throws JobManagerException {

        // Check validity of JobSchedulingConfiguration
        if (jobGroupName == null) {
            throw new JobManagerException("Cannot accertain Job Active State from JobSchedulingConfiguration: [null]");
        }

        boolean retVal = true;
        final JobGroupConfig jobGroupConfig = getJobGroupConfigByJobGroupName(jobGroupName);

        // Check validity of JobSchedulingConfiguration
        if (jobGroupConfig == null) {
            throw new JobManagerException(String.format("No managed JobGroupConfig found for JobGroupName: [%s]",
                    jobGroupName));
        }

        // If Job is not Active - and no override exists - do not schedule
        if (!jobGroupConfig.isJobGroupActive() && overrideGroupConfigActive == null) {
            retVal = false;
        }

        // If JobGroup has an override active status deactivating it - set it to deactivated
        if (overrideGroupConfigActive != null && !overrideGroupConfigActive) {
            retVal = false;
        }

        return retVal;
    }

    /**
     * Log Inactive JobSchedulingConfiguration information when Job is inactive on respectively current
     * ApplicationInstance.
     *
     * @param   jobSchedulingConfig        The {@link JobSchedulingConfiguration} to log active/inactive States and
     *                                     causes for
     * @param   overrideConfigActive       Local active state override for Job
     * @param   overrideGroupConfigActive  Local active state override for JobGroup
     *
     * @throws  JobManagerException  if an unanticipated error occurs
     */
    private void logJobSchedulingConfigActiveState(final JobSchedulingConfiguration jobSchedulingConfig,
            final Boolean overrideConfigActive, final Boolean overrideGroupConfigActive) throws JobManagerException {

        // Check validity of JobSchedulingConfiguration
        if (jobSchedulingConfig == null) {
            throw new JobManagerException("Cannot accertain Job Active State from JobSchedulingConfiguration: [null]");
        }

        final JobConfig jobConfig = jobSchedulingConfig.getJobConfig();

        // If Job is not Active - do not schedule
        if (jobConfig.getJobGroupConfig() != null && !jobConfig.getJobGroupConfig().isJobGroupActive()
                && overrideGroupConfigActive == null) {
            LOG.info("JobGroup: [{}] is not active. Skipping scheduling of Job: [{}].", jobSchedulingConfig,
                jobConfig.getJobGroupConfig());
        }

        // If Job is not Active - do not schedule
        if (overrideGroupConfigActive != null && !overrideGroupConfigActive) {
            LOG.info(
                "JobGroup: [{}] has been deactivated (toggled) on AppInstance: [{}]. Skipping scheduling of Job: [{}].",
                new Object[] {
                    jobConfig.getJobGroupName(), jobConfigSource.getAppInstanceKey(), jobConfig.getJobGroupName()
                });
        }

        // If Job is not Active - do not schedule
        if (!jobConfig.isActive()) {
            LOG.info("Job is not active. Skipping scheduling of Job: [{}].", jobSchedulingConfig);
        }

        // If Job is not Active - do not schedule
        if (overrideConfigActive != null && !overrideConfigActive) {
            LOG.info("Job has been deactivated (toggled) on AppInstance: [{}]. Skipping scheduling of Job: [{}].",
                jobConfigSource.getAppInstanceKey(), jobSchedulingConfig);
        }

        // If Job is not allowed on current AppInstanceKey - do not schedule
        if (!jobConfig.isAllowedAppInstanceKey(jobConfigSource.getAppInstanceKey())) {
            LOG.info("Skipping scheduling of Job: [{}]. Job is not allowed to run on AppInstance: [{}]. "
                    + "Allowed AppInstances are: {}",
                new Object[] {
                    jobSchedulingConfig, jobConfigSource.getAppInstanceKey(), jobConfig.getAllowedAppInstanceKeys()
                });
        }

        // if MaintanenceModes is active - no job is allowed to run
        if (isMainanenceModeActive()) {
            LOG.info("Skipping scheduling of Job: [{}]. Maintanence Mode is active", jobSchedulingConfig);
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
     * @return  The unique name generated for Job
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

                // Check if current JobGroupConfig is managed and make managed if necessary
                // Create a JobGroupConfig instance when JobGroupConfig is NULL
                JobGroupConfig groupConfig = null;
                if (curJobSchedulingConfig.getJobConfig().getJobGroupConfig() != null) {
                    groupConfig = curJobSchedulingConfig.getJobConfig().getJobGroupConfig();
                } else if (curJobSchedulingConfig.getJobConfig().getJobGroupConfig() == null) {
                    groupConfig = new JobGroupConfig(JobGroupConfig.DEFAULT_GROUP_NAME, true, null);
                }

                // Check if a Override exists for the current JobGroup - if an override exists and equals the active
                // state of the group, remove the override
                // This can only happen if a JobGroup has been (de)activated on an instance, and the group
                // (de)activation state is provided by the current Configuration refresh as well
                if (instanceJobGroupConfigOverrides.containsKey(groupConfig)
                        && instanceJobGroupConfigOverrides.get(groupConfig) == groupConfig.isJobGroupActive()) {
                    instanceJobGroupConfigOverrides.remove(groupConfig);
                }

                // if current JobGroup is not managed yet - add it to list of managed JobGroups
                if (!managedJobGroups.contains(groupConfig)) {
                    managedJobGroups.add(groupConfig);
                }

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
                    cancelJob(curJobSchedulingConfig, true);
                }
            }

        } catch (final JobSchedulingConfigurationProviderException e) {
            throw new JobManagerException(String.format(
                    "Refresh of JobSchedulingConfiguration of Jobs failed with Error: [%s]", e.getMessage()), e);
        }
    }

    /**
     * @param   jobSchedulingConfiguration
     * @param   removeFromManagedJobs
     *
     * @throws  JobManagerException
     */
    private void cancelJob(final JobSchedulingConfiguration jobSchedulingConfiguration,
            final boolean removeFromManagedJobs) throws JobManagerException {
        try {
            Preconditions.checkArgument(jobSchedulingConfiguration != null,
                "Cancel Job for NULL JobSchedulingConfiguration is not possible");

            final JobManagerManagedJob managedJob = managedJobs.get(jobSchedulingConfiguration);
            Preconditions.checkArgument(managedJob != null,
                "Cancel Job failed. Could not find Managed Job for JobSchedulingConfiguration: [{}]",
                jobSchedulingConfiguration);
            if (isJobScheduled(managedJob)) {

                managedJob.getQuartzSchedulerFactoryBean().getScheduler().unscheduleJob(managedJob.getQuartzTrigger()
                        .getName(), managedJob.getQuartzTrigger().getGroup());
                LOG.info("Canceled Job: [{}]", managedJob);
            }

            if (removeFromManagedJobs) {

                // Check if there are Job Instances still running before Stopping the Jobs Bean Infrastructure
                LOG.debug("Stopped Job Scheduler for Job: [{}]", managedJob);
                managedJob.getQuartzSchedulerFactoryBean().stop();
                LOG.debug("Stopped Job SchedulerFactory for Job: [{}]", managedJob);
                managedJobs.remove(jobSchedulingConfiguration);
                LOG.debug("Removed Job from Map of managed jobs. Job: [{}]", managedJob);
            }

        } catch (final SchedulerException e) {
            throw new JobManagerException(e);
        } catch (final IllegalArgumentException e) {
            throw new JobManagerException(e);
        }

    }

    @Override
    public JobManagerManagedJob getManagedJob(final String jobName, final String jobGroup) {
        return null;
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
    private boolean isJobScheduled(final JobManagerManagedJob job) throws SchedulerException {
        return job != null && job.getQuartzSchedulerFactoryBean() != null
                && job.getQuartzSchedulerFactoryBean().getScheduler() != null
                && !job.getQuartzSchedulerFactoryBean().getScheduler().isInStandbyMode()
                && job.getQuartzSchedulerFactoryBean().getScheduler().getTrigger(job.getQuartzTrigger().getName(),
                    job.getQuartzTrigger().getGroup()) != null;
    }

    @Override
    public boolean isJobScheduled(final String jobName, final String jobGroup) throws JobManagerException {
        final JobManagerManagedJob job = getManagedJobByJobDetailNameAndJobDetailGroup(jobName, jobGroup);
        Preconditions.checkArgument(job != null, "Could not find Job for JobName: [{}] and JobGroup: [{}]");
        try {
            return isJobScheduled(job);
        } catch (final SchedulerException e) {
            throw new JobManagerException(e);
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
    public List<JobManagerManagedJob> getManagedJobsByClass(final Class<?> jobClass) throws JobManagerException {
        if (jobClass == null) {
            throw new JobManagerException("Cannot get List of Managed Jobs for Job Class: [NULL]");
        }

        final List<JobManagerManagedJob> retVal = Lists.newArrayList();
        for (final JobManagerManagedJob curManagedJob : managedJobs.values()) {
            if (jobClass.getName().equals(curManagedJob.getJobSchedulingConfig().getJobClass())) {
                retVal.add(curManagedJob);
            }
        }

        return retVal;
    }

    @Override
    public List<JobGroupConfig> getManagedJobGroups() {
        return new ArrayList<JobGroupConfig>(managedJobGroups);
    }

    @Override
    public List<JobManagerManagedJob> getScheduledManagedJobs() {
        return Lists.newArrayList(Collections2.filter(getManagedJobs(), new Predicate<JobManagerManagedJob>() {
                        @Override
                        public boolean apply(final JobManagerManagedJob input) {
                            boolean retVal = false;
                            try {
                                retVal = isJobScheduled(input);
                            } catch (final SchedulerException e) {
                                LOG.error(e.getMessage(), e);
                            }

                            return retVal;
                        }
                    }));
    }

    @Override
    public List<JobManagerManagedJob> getUnscheduledManagedJobs() {
        return Lists.newArrayList(Collections2.filter(getManagedJobs(), new Predicate<JobManagerManagedJob>() {
                        @Override
                        public boolean apply(final JobManagerManagedJob input) {
                            boolean retVal = false;
                            try {
                                retVal = !isJobScheduled(input);
                            } catch (final SchedulerException e) {
                                LOG.error(e.getMessage(), e);
                            }

                            return retVal;
                        }
                    }));
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
    public void triggerJob(final JobSchedulingConfiguration jobSchedulingConfiguration, final boolean force)
        throws JobManagerException {
        try {
            Preconditions.checkArgument(jobSchedulingConfiguration != null,
                "Cannot trigger Job for NULL JobSchedulingConfiguration");

            final JobManagerManagedJob job = managedJobs.get(jobSchedulingConfiguration);
            Preconditions.checkArgument(job != null, "Could not find Managed Job for JobSchedulingConfiguration: [{}]",
                jobSchedulingConfiguration);

            final boolean isJobScheduled = isJobScheduled(job);

            // Create a onetime Trigger for Job
            if (isJobScheduled || force) {
                job.getQuartzSchedulerFactoryBean().getScheduler().triggerJob(job.getQuartzJobDetail().getName(),
                    job.getQuartzJobDetail().getGroup());
// final Trigger quartzTrigger = new SimpleTriggerBean();
// ((SimpleTriggerBean) quartzTrigger).setJobDetail(job.getQuartzJobDetail());
// ((SimpleTriggerBean) quartzTrigger).setRepeatCount(0);
// ((SimpleTriggerBean) quartzTrigger).setRepeatInterval(0);
// ((SimpleTriggerBean) quartzTrigger).setStartDelay(0);
// ((SimpleTriggerBean) quartzTrigger).setJobDataMap(job.getQuartzJobDetail().getJobDataMap());
// ((SimpleTriggerBean) quartzTrigger).setBeanName(String.format("%s%s",
// job.getQuartzJobDetail().getName(), "Trigger"));
// quartzTrigger.setName(String.format("%s%s", job.getQuartzJobDetail().getName(), "Trigger"));
// quartzTrigger.setGroup(job.getQuartzJobDetail().getGroup());
// quartzTrigger.setJobName(job.getQuartzJobDetail().getName());
// quartzTrigger.setJobName(job.getQuartzJobDetail().getGroup());
// ((SimpleTriggerBean) quartzTrigger).afterPropertiesSet();
//
// job.getQuartzSchedulerFactoryBean().getScheduler().scheduleJob(job.getQuartzJobDetail(), quartzTrigger);
            } else if (!isJobScheduled && !force) {
                LOG.warn("Job: [{}] was to be triggered, but is not scheduled. Skipping execution",
                    jobSchedulingConfiguration);
            }
        } catch (final SchedulerException e) {
            throw new JobManagerException(e);
        } catch (final IllegalArgumentException e) {
            throw new JobManagerException(e);
        }

    }

    @Override
    public void triggerJob(final String quartzJobDetailName, final String quartzJobDetailGroup, final boolean force)
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

            triggerJob(job.getJobSchedulingConfig(), force);
        } catch (final IllegalArgumentException e) {
            throw new JobManagerException(e);
        }
    }

    @Override
    public void toggleJob(final JobSchedulingConfiguration jobSchedulingConfiguration, final boolean running)
        throws JobManagerException {
        if (jobSchedulingConfiguration == null) {
            throw new JobManagerException("Cannot toggle Job for JobSchedulingConfiguration: [null]");
        }

        final boolean isJobActive = isJobActive(jobSchedulingConfiguration, null, null);

        // Remove potentially existing old override
        instanceJobConfigOverrides.remove(jobSchedulingConfiguration);

        // Add a new one if needed
        if (isJobActive != running) {
            instanceJobConfigOverrides.put(jobSchedulingConfiguration, running);
        }

        // Reschedule affected Job - always cancel potentially already scheduled job
        rescheduleJob(jobSchedulingConfiguration);
    }

    @Override
    public void toggleJobGroup(final String jobGroupName) throws JobManagerException {
        if (jobGroupName == null) {
            throw new JobManagerException("Cannot toggle Job for JobSchedulingConfiguration: [null]");
        }

        // Check if JobGroup is active with and without local override
        final boolean isJobGroupActive = isJobGroupActive(jobGroupName, null);
        final boolean isJobGroupOverrideActive = isJobGroupActive(jobGroupName,
                instanceJobGroupConfigOverrides.get(jobGroupName));

        // Remove old override
        instanceJobGroupConfigOverrides.remove(jobGroupName);

        // Create new Override if necessary
        if (isJobGroupActive == isJobGroupOverrideActive) {
            instanceJobGroupConfigOverrides.put(jobGroupName, !isJobGroupActive);
        }

        // Reschedule all affected Jobs
        for (final JobManagerManagedJob curManagedJob : new HashSet<JobManagerManagedJob>(managedJobs.values())) {
            if (jobGroupName.equals(curManagedJob.getJobSchedulingConfig().getJobConfig().getJobGroupName())) {
                rescheduleJob(curManagedJob.getJobSchedulingConfig());
            }
        }

    }

    @Override
    public void cancelJob(final JobSchedulingConfiguration jobSchedulingConfig) throws JobManagerException {
        cancelJob(jobSchedulingConfig, false);
    }

    @Override
    public void cancelJob(final String quartzJobDetailName, final String quartzJobDetailGroup)
        throws JobManagerException {
        try {
            Preconditions.checkArgument(quartzJobDetailName != null, "Parameter quartJobDetailName must not be NULL");

            final JobManagerManagedJob job = getManagedJobByJobDetailNameAndJobDetailGroup(quartzJobDetailName,
                    quartzJobDetailGroup == null || quartzJobDetailGroup.trim().isEmpty() ? Scheduler.DEFAULT_GROUP
                                                                                          : quartzJobDetailGroup);
            if (job == null) {
                throw new JobManagerException(String.format(
                        "Cancel Job failed. Could not find ManagedJob for JobName: [%s] and JobGroup: [%s]",
                        quartzJobDetailName, quartzJobDetailGroup));
            }

            cancelJob(job.getJobSchedulingConfig());

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
        createJobSchedulingConfigurationPollerExecutor();
        LOG.info("Finished starting up JobManager");
    }

    @Override
    public void shutdown() throws JobManagerException {
        LOG.info("Shutting down JobManager...");
        cancelJobSchedulingConfigurationPollerExecutor();
        LOG.info("Shut down DefaultJobManagers Configuration Poller/Job Rescheduler");
        for (final JobSchedulingConfiguration curConfig : new HashSet<JobSchedulingConfiguration>(managedJobs.keySet())) {
            cancelJob(curConfig, true);
        }

        LOG.info("Shut down Quartz Schedulers");

        // TODO: Wait for running Jobs to finish
        LOG.info("Finished shutting down JobManager");
    }

    @Override
    public OperationMode getOperationMode() {
        return this.operationMode;
    }

    @Override
    public boolean isMainanenceModeActive() {
        return this.operationMode == OperationMode.MAINTENANCE;
    }

    @Override
    public synchronized void setMainanenceModeActive(final boolean isMaintanenceMode) throws JobManagerException {
        if (isMaintanenceMode) {
            this.operationMode = OperationMode.MAINTENANCE;
            cancelAllJobs();
            cancelJobSchedulingConfigurationPollerExecutor();
        } else {
            this.operationMode = OperationMode.NORMAL;
            this.updateSchedulingConfigurationsAndRescheduleManagedJobs();
            createJobSchedulingConfigurationPollerExecutor();
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
            final Job quartzJob = context.getJobInstance();
            if (quartzJob != null && AbstractJob.class.isInstance(quartzJob)) {
                ((AbstractJob) quartzJob).setJobConfig(currentJob.getJobSchedulingConfig().getJobConfig());
                try {
                    ((AbstractJob) quartzJob).setJobGroupConfig(getJobGroupConfigByJobGroupName(
                            currentJob.getJobSchedulingConfig().getJobConfig().getJobGroupName()));
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

    @Override
    public void jobWasExecuted(final JobExecutionContext context, final JobExecutionException jobException) {
        final JobManagerManagedJob currentJob = getManagedJobByJobDetail(context.getJobDetail());
        if (currentJob != null) {
            final Job quartzJob = context.getJobInstance();
            if (quartzJob != null && RunningWorker.class.isInstance(quartzJob)) {
                currentJob.onFinishRunningWorker((RunningWorker) quartzJob);
            }
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
