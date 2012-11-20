package de.zalando.zomcat.jobs.management.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.zalando.zomcat.OperationMode;
import de.zalando.zomcat.configuration.AppInstanceKeySource;
import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.JobGroupConfig;
import de.zalando.zomcat.jobs.RunningWorker;
import de.zalando.zomcat.jobs.management.JobManager;
import de.zalando.zomcat.jobs.management.JobManagerException;
import de.zalando.zomcat.jobs.management.JobManagerManagedJob;
import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;
import de.zalando.zomcat.jobs.management.JobSchedulingConfigurationProvider;
import de.zalando.zomcat.jobs.management.JobSchedulingConfigurationProviderException;
import de.zalando.zomcat.jobs.management.JobSchedulingConfigurationType;

/**
 * Default Implementation of {@link JobManager} interface. Simple component that manages Quartz Jobs. Features include:
 * on demand scheduling, on demand rescheduling, on demand job cancelation, Maintenance mode support, job history incl
 * results, per AppInstance job and job group (de)activation override, etc.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public abstract class AbstractJobManager implements JobManager, JobListener, Runnable, ApplicationContextAware {

    /**
     * Logger for this class.
     */
    private static final transient Logger LOG = LoggerFactory.getLogger(AbstractJobManager.class);

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
     * Executor PoolSize JobDetailMap Property Name.
     */
    private static final transient String QUEUE_SIZE_JOB_DATA_KEY = "queue";

    /**
     * Predicate for Scheduled Jobs - not a new Instance of Predicate for each Call to using method.
     */
    private final Predicate<JobManagerManagedJob> scheduledJobsPredicate = new Predicate<JobManagerManagedJob>() {

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
    };

    /**
     * Predicate for Unscheduled Jobs - not a new Instance of Predicate for each Call to using method.
     */
    private final Predicate<JobManagerManagedJob> unscheduledJobscPredicate = Predicates.not(scheduledJobsPredicate);

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
     * Threadpool Executor for {@link JobSchedulingConfiguration} polling and Job (re)scheduling.
     */
    private ScheduledThreadPoolExecutor schedulingConfigPollingExecutor;

    /**
     * Spring {@link ApplicationContext} - this Bean is {@link ApplicationContextAware}.
     */
    private ApplicationContext applicationContext;

    /**
     * Started State of JobManager.
     */
    private final AtomicBoolean jobManagerStarted;

    /**
     * JobConfigSource.
     */
    @Autowired
    private AppInstanceKeySource appInstanceKeySource;

    /**
     * Default Constructor.
     *
     * @throws  JobManagerException  if any error occurs during instantiation
     */
    public AbstractJobManager() throws JobManagerException {
        managedJobs = Maps.newConcurrentMap();
        managedJobGroups = Sets.newCopyOnWriteArraySet();
        jobNameSequence = Maps.newConcurrentMap();
        instanceJobConfigOverrides = Maps.newConcurrentMap();
        instanceJobGroupConfigOverrides = Maps.newConcurrentMap();
        jobManagerStarted = new AtomicBoolean(false);
    }

    /**
     * Create a JobSchedulingConfigurationPoller {@link Executor} that polls the {@link JobSchedulingConfiguration}s
     * periodically (every minute).
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
        while (schedulingConfigPollingExecutor.getActiveCount() > 0) {
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                LOG.error(
                    "An error occured waiting for JobSchedulingConfiguration Poller/Rescheduler Thread to finish");
            }
        }

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

            // Cancel Job if a Reschedule has been requested
            if (reschedule && managedJobs.containsKey(jobSchedulingConfig)) {
                cancelJob(jobSchedulingConfig, true);
            }

            // Put Scheduler into managed Map of Jobs
// if (!managedJobs.containsKey(jobSchedulingConfig)) {
            managedJobs.put(jobSchedulingConfig, createManagedJob(jobSchedulingConfig));
// }

            // Get current Managed Job - must be managed at this point
            final JobManagerManagedJob managedJob = managedJobs.get(jobSchedulingConfig);

            // if a Job Config override exists with the same active state as the job itself - remove the override
            final boolean isJobActive = isJobActive(jobSchedulingConfig, null, null);
            if (this.instanceJobConfigOverrides.containsKey(jobSchedulingConfig)
                    && this.instanceJobConfigOverrides.get(jobSchedulingConfig) == isJobActive) {
                instanceJobConfigOverrides.remove(jobSchedulingConfig);
            }

            // if the Job is Active - schedule it, otherwise log WHY job is inactive
            if (isJobActive(jobSchedulingConfig, instanceJobConfigOverrides.get(jobSchedulingConfig),
                        instanceJobGroupConfigOverrides.get(jobSchedulingConfig.getJobConfig().getJobGroupName()))) {
                managedJob.getQuartzTrigger().setStartTime(new Date(System.currentTimeMillis()));

                // Schedule the Jobs Trigger
                managedJob.getQuartzScheduler().scheduleJob(managedJob.getQuartzJobDetail(),
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
        } catch (final SchedulerException e) {
            throw new JobManagerException(e);
        }
    }

    /**
     * Create a Managed Job Instance for given {@link JobSchedulingConfiguration} instance.
     *
     * @param   jobSchedulingConfig  The {@link JobSchedulingConfiguration} instance to use for ManagedJobCreation
     *
     * @throws  ClassNotFoundException  if the JobClass cannot be loaded or found
     * @throws  ParseException          if the supplied CRON Expression is invalid or could not be parsed
     * @throws  JobManagerException     if any other unanticipated error occurs
     */
    private JobManagerManagedJob createManagedJob(final JobSchedulingConfiguration jobSchedulingConfig)
        throws ParseException, JobManagerException, ClassNotFoundException {

        // Create Job Detail from Scheduler Configuration
        final JobDetail jobDetail = toQuartzJobDetailFromJobSchedulingConfig(jobSchedulingConfig);

        // Create Quartz Trigger
        final Trigger quartzTrigger = createQuartzTrigger(jobSchedulingConfig, jobDetail);

        // Default Executor Size - default avoids multiple Executions of Same JOB
        final int poolSize = parseJobDataInteger(jobSchedulingConfig.getJobData(), POOL_SIZE_JOB_DATA_KEY, 1,
                "invalid thread pool size (not an integer)");
        final int queueSize = parseJobDataInteger(jobSchedulingConfig.getJobData(), QUEUE_SIZE_JOB_DATA_KEY, 0,
                "invalid thread queue size (not an integer)");

        try {
            return onCreateManagedJob(jobSchedulingConfig, jobDetail, quartzTrigger, poolSize, queueSize);
        } catch (final Exception e) {
            throw new JobManagerException(e.getMessage(), e);
        }
    }

    /**
     * Parse an Integer that may be contained in given JobData Map.
     *
     * @param   jobDataMap     The {@link Map} containing Metadata as String -> String combination
     * @param   jobDataMapKey  The {@link Map} key to use for Data extraction
     * @param   defaultValue   The Default value to return if queried metadata key is not available in given {@link Map}
     * @param   errorMessage   The Error Message to use when parsing of assumed integer value fails
     *
     * @return  The parsed Integer or default given
     *
     * @throws  IllegalArgumentException  - if a parsing error occurs
     */
    private int parseJobDataInteger(final Map<String, String> jobDataMap, final String jobDataMapKey,
            final int defaultValue, final String errorMessage) {
        int retVal = defaultValue;
        if (jobDataMap != null && jobDataMap.containsKey(jobDataMapKey)) {
            try {
                retVal = Integer.valueOf(jobDataMap.get(jobDataMapKey));
            } catch (final NumberFormatException nfe) {
                throw new IllegalArgumentException(errorMessage, nfe);
            }
        }

        return retVal;
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
     * @throws  ParseException       if the CRON Expression could not be parsed
     * @throws  JobManagerException  if the {@link JobSchedulingConfiguration} contains unsupported Trigger Type
     */
    private Trigger createQuartzTrigger(final JobSchedulingConfiguration jobSchedulingConfig, final JobDetail jobDetail)
        throws JobManagerException, ParseException {

        // Create Quartz Trigger from Scheduler Configuration
        Trigger quartzTrigger = null;
        switch (jobSchedulingConfig.getJobType()) {

            case CRON :
                quartzTrigger = new CronTrigger();
                ((CronTrigger) quartzTrigger).setCronExpression(jobSchedulingConfig.getCronExpression());
                break;

            case SIMPLE :
                quartzTrigger = new SimpleTrigger();
                ((SimpleTrigger) quartzTrigger).setStartTime(new Date(
                        System.currentTimeMillis() + jobSchedulingConfig.getStartDelayMillis()));
                ((SimpleTrigger) quartzTrigger).setRepeatInterval(jobSchedulingConfig.getIntervalMillis());
                break;

            default :
                throw new JobManagerException(String.format("Unsupported Job Scheduling Type: %s",
                        jobSchedulingConfig.getJobType()));

        }

        // Common Trigger Setup
        quartzTrigger.setJobDataMap(jobDetail.getJobDataMap());
        quartzTrigger.setName(String.format("%s%s", jobDetail.getName(), "Trigger"));
        quartzTrigger.setGroup(jobDetail.getGroup());
        quartzTrigger.setJobName(jobDetail.getName());
        quartzTrigger.setJobGroup(jobDetail.getGroup());

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
        if (!jobConfig.isAllowedAppInstanceKey(appInstanceKeySource.getAppInstanceKey())) {
            retVal = false;
        }

        // if Maintenance Mode is active - no job is allowed to run
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
    protected final JobGroupConfig getJobGroupConfigByJobGroupName(final String jobGroupName)
        throws JobManagerException {
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
                    jobConfig.getJobGroupName(), appInstanceKeySource.getAppInstanceKey(), jobConfig.getJobGroupName()
                });
        }

        // If Job is not Active - do not schedule
        if (!jobConfig.isActive()) {
            LOG.info("Job is not active. Skipping scheduling of Job: [{}].", jobSchedulingConfig);
        }

        // If Job is not Active - do not schedule
        if (overrideConfigActive != null && !overrideConfigActive) {
            LOG.info("Job has been deactivated (toggled) on AppInstance: [{}]. Skipping scheduling of Job: [{}].",
                appInstanceKeySource.getAppInstanceKey(), jobSchedulingConfig);
        }

        // If Job is not allowed on current AppInstanceKey - do not schedule
        if (!jobConfig.isAllowedAppInstanceKey(appInstanceKeySource.getAppInstanceKey())) {
            LOG.info("Skipping scheduling of Job: [{}]. Job is not allowed to run on AppInstance: [{}]. "
                    + "Allowed AppInstances are: {}",
                new Object[] {
                    jobSchedulingConfig, appInstanceKeySource.getAppInstanceKey(), jobConfig.getAllowedAppInstanceKeys()
                });
        }

        // if Maintenance Mode is active - no job is allowed to run
        if (isMainanenceModeActive()) {
            LOG.info("Skipping scheduling of Job: [{}]. Maintenance Mode is active", jobSchedulingConfig);
        }
    }

    /**
     * Validate given {@link JobSchedulingConfiguration}.
     *
     * @param   jobSchedulingConfig  the {@link JobSchedulingConfiguration} to validate
     *
     * @throws  IllegalArgumentException  if any validation error occurs
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
            Preconditions.checkArgument(jobSchedulingConfig.getStartDelayMillis() != null,
                "JobSchedulingConfig.startDelayMS cannot be NULL on SIMPLE Job");
            Preconditions.checkArgument(jobSchedulingConfig.getIntervalMillis() != null,
                "JobSchedulingConfig.intervalMS cannot be NULL on SIMPLE Job");
        }
    }

    /**
     * Create a Quartz JobDetail for given JobSchedulingConfig.
     *
     * @param   config  The {@link JobSchedulingConfiguration} to create Quartz JobDetail for
     *
     * @return  The Quartz {@link JobDetail} instance
     *
     * @throws  ClassNotFoundException  if the Jobs {@link Class} cannot be loaded or found
     */
    private JobDetail toQuartzJobDetailFromJobSchedulingConfig(final JobSchedulingConfiguration config)
        throws ClassNotFoundException {
        final JobDetail retVal = new JobDetail(getJobName(config), config.getJobJavaClass());
        retVal.setJobDataMap(new JobDataMap(config.getJobData()));
        retVal.setDurability(false);

        // Set JobGroup if there is one
        retVal.setGroup(config.getJobConfig().getJobGroupName());
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
     * @throws  ClassNotFoundException  if the supplied JobClass cannot be loaded or found
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
     * Check given {@link JobManagerManagedJob} if it matches given Quart JobDetail Name and Group.
     *
     * @param   managedJob            The {@link JobManagerManagedJob} to check
     * @param   quartzJobDetailName   The Quartz {@link JobDetail} name to check Job for
     * @param   quartzJobDetailGroup  The Quartz {@link JobDetail} group to check Job for
     *
     * @return  <code>true</code> if {@link JobManagerManagedJob} to check is an exact match for supplied Quartz
     *          JobDetail Name and Group, <code>false/ <code>otherwise
     */
    private boolean isJobMatchesQuartzJobDetailNameAndGroup(final JobManagerManagedJob managedJob,
            final String quartzJobDetailName, final String quartzJobDetailGroup) {
        return managedJob != null && managedJob.getQuartzJobDetail() != null
                && managedJob.getQuartzJobDetail().getName().equals(quartzJobDetailName)
                && managedJob.getQuartzJobDetail().getGroup().equals(quartzJobDetailGroup);

    }

    /**
     * Is a reschedule required for given {@link JobSchedulingConfiguration} and {@link JobGroupConfig}.
     *
     * @param   jobSchedulingConfigurationToCheck  The {@link JobSchedulingConfiguration} to check
     * @param   jobGroupConfig                     The {@link JobGroupConfig} to check
     *
     * @return
     *
     * @throws  SchedulerException   if Quartz throws a {@link SchedulerException} on check
     * @throws  JobManagerException  if an unanticipated Error occurs checking Jobs active State with {@link JobManager}
     */
    private boolean isJobRescheduleRequired(final JobSchedulingConfiguration jobSchedulingConfigurationToCheck,
            final JobGroupConfig jobGroupConfig) throws SchedulerException, JobManagerException {
        final JobManagerManagedJob managedJob = this.managedJobs.get(jobSchedulingConfigurationToCheck);
        final boolean isSchedulingConfigAltered = !jobSchedulingConfigurationToCheck.isEqual(
                managedJob.getJobSchedulingConfig());
        final boolean isScheduled = isJobScheduled(managedJob);
        final boolean isActive = isJobActive(jobSchedulingConfigurationToCheck,
                instanceJobConfigOverrides.get(jobSchedulingConfigurationToCheck),
                instanceJobGroupConfigOverrides.get(jobGroupConfig));

        return isSchedulingConfigAltered || isScheduled ^ isActive;
    }

    /**
     * This method reads {@link JobSchedulingConfiguration}s from {@link JobSchedulingConfigurationProvider} and
     * (re)schedules/cancels scheduled Jobs accordingly. Only a single thread must be allowed to to this at any given
     * time.
     *
     * @throws  JobManagerException  if any error occurs updateing the {@link JobSchedulingConfiguration}s via
     *                               {@link JobSchedulingConfigurationProvider}
     */
    private void updateSchedulingConfigurationsAndRescheduleManagedJobs() throws JobManagerException {
        try {

            if (isMainanenceModeActive()) {
                LOG.info("Maintenance Mode is active - skipping Update of SchedulingConfigurations");
                return;
            }

            // Refresh Active Configurations via
            // DelegatingJobSchedulingConfigProvider
            final List<JobSchedulingConfiguration> currentProvidedConfigs =
                delegatingJobSchedulingConfigProvider.provideSchedulerConfigs();

            LOG.info("Provider retrieved: [{}] SchedulingConfigs. Starting Job Scheduling Update...",
                currentProvidedConfigs.size());

            // Process provided JobSchedulingConfigurations and schedule and manage previously unmanaged but currently
            // provided jobs
            processProvidedJobSchedulingConfigurations(currentProvidedConfigs);

            // Process managed Jobs - unschedule and remove managed jobs that have not been provided anymore
            processManagedJobSchedulingConfigurations(currentProvidedConfigs);

            LOG.info("Finished Job Scheduling Update - JobManager now contains: [{}] managed jobs. "
                    + "JobCount scheduled locally: [{}], JobCount not scheduled locally: [{}]",
                new Object[] {managedJobs.size(), getScheduledManagedJobs().size(), getUnscheduledManagedJobs().size()});
        } catch (final JobSchedulingConfigurationProviderException e) {
            throw new JobManagerException(String.format(
                    "Refresh of JobSchedulingConfiguration of Jobs failed with Error: [%s]", e.getMessage()), e);
        }
    }

    /**
     * Process all Managed Jobs with currently provided {@link JobSchedulingConfiguration} instances. Unschedule and
     * remove all Jobs that are managed but unavailable in provided List of {@link JobSchedulingConfiguration}s
     *
     * @param  currentProvidedConfigs  The currently provided {@link JobSchedulingConfiguration}s
     */
    private void processManagedJobSchedulingConfigurations(
            final List<JobSchedulingConfiguration> currentProvidedConfigs) {

        // Loop through already scheduled Jobs and check if a configuration
        // was removed from provided
        // configuration List
        int countSuccess = 0;

        final Set<JobSchedulingConfiguration> currentlyScheduledJobs = new HashSet<JobSchedulingConfiguration>(
                managedJobs.keySet());
        for (final JobSchedulingConfiguration curJobSchedulingConfig : currentlyScheduledJobs) {
            try {
                if (!currentProvidedConfigs.contains(curJobSchedulingConfig)) {
                    cancelJob(curJobSchedulingConfig, true);
                }

                countSuccess++;
            } catch (final JobManagerException e) {
                LOG.error("An error occured processing JobSchedulingConfiguration during Configuration "
                        + "Update/(re)schedule of Jobs. Error was: [{}]", e.getMessage(), e);
            }
        }

        // Add a Warning if an error occured on at least one JobSchedulingConfiguration
        if (countSuccess != currentlyScheduledJobs.size()) {
            LOG.warn(
                "Validating [{}] scheduled Jobs agains List of provided JobSchedulingConfigurations - [{}] of which were erroneous.",
                currentlyScheduledJobs.size(), currentlyScheduledJobs.size() - countSuccess);
        }
    }

    /**
     * Process all currently provided {@link JobSchedulingConfiguration}s and schedule all Jobs that are unscheduled but
     * allowed to run on respectively current application instance.
     *
     * @param  currentProvidedConfigs  The currently provided {@link JobSchedulingConfiguration}s
     */
    private void processProvidedJobSchedulingConfigurations(
            final List<JobSchedulingConfiguration> currentProvidedConfigs) {

        // Loop through provided Configurations and check if it is either not scheduled at all or if a reschedule
        // is required
        int countSuccess = 0;

        // Check if current JobGroupConfig is managed and make managed if necessary
        for (final JobSchedulingConfiguration curJobSchedulingConfig : currentProvidedConfigs) {
            try {

                // Create a JobGroupConfig instance when JobGroupConfig is NULL
                JobGroupConfig groupConfig = null;
                if (curJobSchedulingConfig.getJobConfig().getJobGroupConfig() != null) {
                    groupConfig = curJobSchedulingConfig.getJobConfig().getJobGroupConfig();
                } else if (curJobSchedulingConfig.getJobConfig().getJobGroupConfig() == null) {
                    groupConfig = new JobGroupConfig(curJobSchedulingConfig.getJobConfig().getJobGroupName(), true,
                            null);
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
                    this.scheduleOrRescheduleJob(curJobSchedulingConfig, false);
                } else if (this.managedJobs.containsKey(curJobSchedulingConfig)
                        && isJobRescheduleRequired(curJobSchedulingConfig, groupConfig)) {
                    this.scheduleOrRescheduleJob(curJobSchedulingConfig, true);
                }

                countSuccess++;
            } catch (final JobManagerException e) {
                LOG.error("An error occured processing JobSchedulingConfiguration during Configuration "
                        + "Update/(re)schedule of Jobs. Error was: [{}]", e.getMessage(), e);
            } catch (final SchedulerException e) {
                LOG.error("An error occured processing JobSchedulingConfiguration during Configuration "
                        + "Update/(re)schedule of Jobs. Error was: [{}]", e.getMessage(), e);
            }
        }

        // Add a Warning if an error occured on at least one JobSchedulingConfiguration
        if (countSuccess != currentProvidedConfigs.size()) {
            LOG.warn("Processed [{}] JobSchedulingConfigurations - [{}] of which were erroneous.",
                currentProvidedConfigs.size(), currentProvidedConfigs.size() - countSuccess);
        }
    }

    /**
     * Cancel Job identified by {@link JobSchedulingConfiguration}.
     *
     * @param   jobSchedulingConfiguration  The {@link JobSchedulingConfiguration} of Job to cancel
     * @param   removeFromManagedJobs       if <code>true</code> remove job from Map of managed Jobs after cancel,
     *                                      <code>false <code>will only cancel Job but keep it managed
     *
     * @throws  JobManagerException  if any error occurs canceling the Job
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
            try {
                if (isJobScheduled(managedJob)) {
                    managedJob.getQuartzScheduler().deleteJob(managedJob.getQuartzJobDetail().getName(),
                        managedJob.getQuartzJobDetail().getGroup());
                    LOG.info("Canceled Job: [{}]", managedJob);
                }
            } catch (final SchedulerException e) {
                throw new JobManagerException(e);
            } finally {
                onCancelJob(managedJob, removeFromManagedJobs);
            }

        } catch (final IllegalArgumentException e) {
            throw new JobManagerException(e);
        }

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
        return job != null && job.getQuartzScheduler() != null && !job.getQuartzScheduler().isInStandbyMode()
                && job.getQuartzScheduler().getJobDetail(job.getQuartzJobDetail().getName(),
                    job.getQuartzJobDetail().getGroup()) != null;
    }

    /**
     * Callback used to create {@link JobManagerManagedJob} instance in respective Implementation of {@link JobManager}
     * interface.
     *
     * @param   jobSchedulingConfiguration  The {@link JobSchedulingConfiguration}
     * @param   jobDetail                   The {@link JobDetail}
     * @param   trigger                     The {@link Trigger}
     *
     * @return  The {@link JobManagerManagedJob} implementation for dedicated {@link JobManager} implementation
     *
     * @throws  Exception  if any error occurs
     */
    protected abstract JobManagerManagedJob onCreateManagedJob(JobSchedulingConfiguration jobSchedulingConfiguration,
            JobDetail jobDetail, Trigger trigger, int poolSize, int queueSize) throws Exception;

    /**
     * Callback used to cancel {@link JobManagerManagedJob}.
     *
     * @param   managedJob             The {@link JobManagerManagedJob} to cancel
     * @param   removeFromManagedJobs  flag indicating whether or not to remove job from Map of managed Jobs
     *
     * @throws  JobManagerException  if any unanticipated error occurs executing the callback
     */
    protected abstract void onCancelJob(final JobManagerManagedJob managedJob, final boolean removeFromManagedJobs)
        throws JobManagerException;

    /**
     * Internal Getter for {@link ApplicationContext}.
     *
     * @return  The Spring {@link ApplicationContext}
     */
    protected final ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Callback for OnStartup Event.
     *
     * @throws  JobManagerException  if any unanticipated error occurs during execution of callback
     */
    protected void onStartup() throws JobManagerException {
        // Do nothing by default
    }

    /**
     * Callback for OnStartup Event.
     *
     * @throws  JobManagerException  if any unanticipated error occurs during execution of callback
     */
    protected void onShutdown() throws JobManagerException {
        // Do nothing by default
    }

    /**
     * Get {@link JobManagerManagedJob} for given JobDetail.
     *
     * @param   jobDetail  The Quartz {@link JobDetail} to find {@link JobManagerManagedJob} for
     *
     * @return  The matching {@link JobManagerManagedJob} or <code>null</code> if no match could be found
     */
    protected final JobManagerManagedJob getManagedJobByJobDetail(final JobDetail jobDetail) {
        JobManagerManagedJob retVal = null;
        for (final JobManagerManagedJob curManagedJob : managedJobs.values()) {
            if (isJobMatchesQuartzJobDetailNameAndGroup(curManagedJob, jobDetail.getName(), jobDetail.getGroup())) {
                retVal = curManagedJob;
                break;
            }
        }

        return retVal;
    }

    /**
     * Get {@link JobManagerManagedJob} for given JobDetail Name and Group.
     *
     * @param   quartzJobDetailName   The Quartz {@link JobDetail} name
     * @param   quartzJobDetailGroup  The Quartz {@link JobDetail} group
     *
     * @return  matching {@link JobManagerManagedJob} or <code>null</code> if no {@link JobManagerManagedJob} can be
     *          found for given parameters
     */
    protected final JobManagerManagedJob getManagedJobByJobDetailNameAndJobDetailGroup(final String quartzJobDetailName,
            final String quartzJobDetailGroup) {
        JobManagerManagedJob retVal = null;
        for (final JobManagerManagedJob curManagedJob : managedJobs.values()) {
            if (isJobMatchesQuartzJobDetailNameAndGroup(curManagedJob, quartzJobDetailName, quartzJobDetailGroup)) {
                retVal = curManagedJob;
                break;
            }
        }

        return retVal;
    }

    @Override
    public final JobManagerManagedJob getManagedJob(final JobSchedulingConfiguration jobSchedulingConfiguration)
        throws JobManagerException {
        try {
            Preconditions.checkArgument(jobSchedulingConfiguration != null,
                "JobSchedulingConfiguration to find Job by cannot be null");
            return managedJobs.get(jobSchedulingConfiguration);
        } catch (final IllegalArgumentException e) {
            throw new JobManagerException(e.getMessage(), e);
        }
    }

    @Override
    public final JobManagerManagedJob getManagedJob(final String quartzJobDetailName, final String quartzJobDetailGroup)
        throws JobManagerException {
        try {
            Preconditions.checkArgument(quartzJobDetailName != null,
                "Quartz JobDetail Name to find Job for cannot be null");
            Preconditions.checkArgument(quartzJobDetailGroup != null,
                "Quartz JobDetail Group to find Job for cannot be null");

            JobManagerManagedJob retVal = null;
            for (final JobManagerManagedJob curManagedJob : managedJobs.values()) {
                if (quartzJobDetailName.equals(curManagedJob.getQuartzJobDetail().getName())
                        && quartzJobDetailGroup.equals(curManagedJob.getQuartzJobDetail().getGroup())) {
                    retVal = curManagedJob;
                    break;
                }
            }

            return retVal;
        } catch (final IllegalArgumentException e) {
            throw new JobManagerException(e.getMessage(), e);
        }
    }

    @Override
    public final boolean isJobScheduled(final String jobName, final String jobGroup) throws JobManagerException {
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
    public final void setDelegatingJobSchedulingConfigProvider(
            final JobSchedulingConfigurationProvider delegatingJobSchedulingConfigProvider) {
        this.delegatingJobSchedulingConfigProvider = delegatingJobSchedulingConfigProvider;
    }

    /**
     * {@link JobManager} interface implementations.
     */
    @Override
    public final List<JobManagerManagedJob> getManagedJobs() {
        return new ArrayList<JobManagerManagedJob>(managedJobs.values());
    }

    protected final Map<JobSchedulingConfiguration, JobManagerManagedJob> getManagedJobsInternal() {
        return managedJobs;
    }

    @Override
    public final List<JobManagerManagedJob> getManagedJobsByClass(final Class<?> jobClass) throws JobManagerException {
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
    public final List<JobGroupConfig> getManagedJobGroups() {
        return new ArrayList<JobGroupConfig>(managedJobGroups);
    }

    @Override
    public final List<JobManagerManagedJob> getScheduledManagedJobs() {
        return Lists.newArrayList(Collections2.filter(getManagedJobs(), scheduledJobsPredicate));
    }

    @Override
    public final List<JobManagerManagedJob> getUnscheduledManagedJobs() {
        return Lists.newArrayList(Collections2.filter(getManagedJobs(), unscheduledJobscPredicate));
    }

    @Override
    public boolean isStarted() {
        return jobManagerStarted.get();
    }

    @Override
    public final void scheduleJob(final JobSchedulingConfiguration jobSchedulingConfig) throws JobManagerException {
        scheduleOrRescheduleJob(jobSchedulingConfig, false);
    }

    @Override
    public final void rescheduleJob(final JobSchedulingConfiguration jobSchedulingConfig) throws JobManagerException {
        scheduleOrRescheduleJob(jobSchedulingConfig, true);
    }

    @Override
    public final void triggerJob(final JobSchedulingConfiguration jobSchedulingConfiguration, final boolean force)
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
                if (isJobScheduled
                        || job.getQuartzScheduler().getJobDetail(job.getQuartzJobDetail().getName(),
                            job.getQuartzJobDetail().getGroup()) != null) {
                    job.getQuartzScheduler().triggerJob(job.getQuartzJobDetail().getName(),
                        job.getQuartzJobDetail().getGroup());
                } else {

                    // Create One Time Trigger, schedule Job with Trigger on the Jobs Scheduler
                    final Trigger trigger = TriggerUtils.makeImmediateTrigger(0, 0);
                    trigger.setName(trigger.getJobName() + "TriggerImmediate");
                    trigger.setStartTime(new Date(System.currentTimeMillis()));
                    job.getQuartzScheduler().scheduleJob(job.getQuartzJobDetail(), trigger);
                }

                LOG.info("Triggered Job: [{}]", job.getJobSchedulingConfig());

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
    public final void triggerJob(final String quartzJobDetailName, final String quartzJobDetailGroup,
            final boolean force) throws JobManagerException {
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
    public final void toggleJob(final JobSchedulingConfiguration jobSchedulingConfiguration, final boolean running)
        throws JobManagerException {
        try {

            // Check parameter not null
            Preconditions.checkArgument(jobSchedulingConfiguration != null,
                "Cannot toggle Job for JobSchedulingConfiguration: [null]");

            final JobManagerManagedJob managedJob = managedJobs.get(jobSchedulingConfiguration);

            // Check that Parameter identifies a Managed Job
            Preconditions.checkArgument(managedJob != null,
                String.format("Could not find Managed Job for JobSchedulingConfiguration: [%s]",
                    jobSchedulingConfiguration));

            final boolean isJobActive = isJobActive(jobSchedulingConfiguration, null, null);

            // Remove potentially existing old override
            instanceJobConfigOverrides.remove(jobSchedulingConfiguration);

            // Add a new one if needed
            if (isJobActive != running) {
                instanceJobConfigOverrides.put(jobSchedulingConfiguration, running);
            }

            // Reschedule affected Job - always cancel potentially already scheduled job
            rescheduleJob(jobSchedulingConfiguration);
        } catch (final IllegalArgumentException e) {
            throw new JobManagerException(e.getMessage(), e);
        }
    }

    @Override
    public final void toggleJobGroup(final String jobGroupName) throws JobManagerException {
        try {
            Preconditions.checkArgument(jobGroupName != null, "Cannot toggle JobGroup: [null]");

            final JobGroupConfig config = getJobGroupConfigByJobGroupName(jobGroupName);

            Preconditions.checkArgument(config != null,
                String.format("Could not find Managed Job Group for JobGroupName: [%s]", jobGroupName));

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
        } catch (final IllegalArgumentException e) {
            throw new JobManagerException(e.getMessage(), e);
        }
    }

    @Override
    public final void cancelJob(final JobSchedulingConfiguration jobSchedulingConfig) throws JobManagerException {
        cancelJob(jobSchedulingConfig, false);
    }

    @Override
    public final void cancelJob(final String quartzJobDetailName, final String quartzJobDetailGroup)
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
    public final void cancelAllJobs() throws JobManagerException {
        for (final JobSchedulingConfiguration curConfig : managedJobs.keySet()) {
            cancelJob(curConfig);
        }
    }

    @Override
    public final synchronized void updateJobSchedulingConfigurations() throws JobManagerException {
        this.updateSchedulingConfigurationsAndRescheduleManagedJobs();
    }

    @Override
    public final synchronized void startup() throws JobManagerException {
        if (isStarted()) {
            throw new JobManagerException("Cannot start already started JobManager.");
        }

        LOG.info("Starting up JobManager");

        Preconditions.checkArgument(delegatingJobSchedulingConfigProvider != null,
            "DefaultJobManager has no DelegatingJobSchedulingConfigProvider set");

        // Use the Callback to execute implementation specific Logic - if there is any
        onStartup();

        // Set the Operation Mode to Normal
        this.operationMode = OperationMode.NORMAL;

        // Do Initial Scheduling
        LOG.info("Fetching/Scheduling initial Scheduling Configuration...");
        this.updateJobSchedulingConfigurations();
        LOG.info("Starting Job Scheduling Configuration update Poller...");

        // Schedule the Rescheduling Thread
        createJobSchedulingConfigurationPollerExecutor();
        LOG.info("Finished starting up JobManager");

        // Set started State
        jobManagerStarted.set(true);
    }

    @Override
    public final synchronized void shutdown() throws JobManagerException {
        if (!isStarted()) {
            throw new JobManagerException("Cannot shutdown unstarted JobManager.");
        }

        LOG.info("Shutting down JobManager...");

        // Shutdown COnfiguration Updater/Rescheduler Thread(s)
        if (!isMainanenceModeActive()) {
            cancelJobSchedulingConfigurationPollerExecutor();
            LOG.info("Shut down DefaultJobManagers Configuration Poller/Job Rescheduler");
        }

        // Cancel all Jobs without removing them from Map of managed Jobs
        for (final JobSchedulingConfiguration curConfig : new HashSet<JobSchedulingConfiguration>(managedJobs.keySet())) {
            cancelJob(curConfig, false);
        }

        LOG.info("Canceled all Jobs");
        LOG.info("Waiting for active RunningWorkers to finish...");

        for (final JobManagerManagedJob curManagedJob : getManagedJobs()) {
            if (curManagedJob.getRunningWorkerCount() > 0) {
                LOG.info("Job: [{}] still has [{}] active RunningWorker instances",
                    curManagedJob.getJobSchedulingConfig(), curManagedJob.getRunningWorkerCount());
            }
        }

        // Wait for all active Workers to complete before continueing the Shutdown thread that called this method
        // Spring will be blocked during its shutdown if there are still Managed Jobs Running
        boolean continueWaiting = true;
        while (continueWaiting) {
            continueWaiting = false;

            // Check if there is at least one Job with a RunningWorkerCount > 0
            for (final JobManagerManagedJob curManagedJob : getManagedJobs()) {
                if (curManagedJob.getRunningWorkerCount() > 0) {
                    continueWaiting = true;
                    break;
                }
            }

            // Sleep for a second - if waiting is required
            if (continueWaiting) {

                try {
                    Thread.sleep(1000);
                } catch (final InterruptedException e) {
                    LOG.error("An error occured waiting for RunningWorkers to complete");
                }
            }
        }

        LOG.info("All RunningWorkers finished, continuing shutdown of JobManager...");

        // Fully cancel Jobs (remove Jobs from Map of managed Jobs) - shutdown Quartz infrastructure (schedulers,
        // triggers, jobdetails etc)
        for (final JobSchedulingConfiguration curConfig : new HashSet<JobSchedulingConfiguration>(managedJobs.keySet())) {
            cancelJob(curConfig, true);
        }

        LOG.info("Shut down all Quartz Job Schedulers");

        // Use the Callback to execute implementation specific Logic - if there is any
        onShutdown();

        // Cleanup Instance Override Statuus
        instanceJobConfigOverrides.clear();
        instanceJobGroupConfigOverrides.clear();

        // Reset the JobName Sequence Map
        jobNameSequence.clear();

        // Remove all managed JobGroups
        managedJobGroups.clear();

        // Remove all remaining Managed Jobs - there should not be any
        managedJobs.clear();

        LOG.info("Finished shutting down JobManager");

        // Set started State
        jobManagerStarted.set(false);
    }

    @Override
    public final OperationMode getOperationMode() {
        return this.operationMode;
    }

    @Override
    public final boolean isMainanenceModeActive() {
        return this.operationMode == OperationMode.MAINTENANCE;
    }

    @Override
    public final synchronized void setMaintenanceModeActive(final boolean isMaintenanceMode)
        throws JobManagerException {
        if (isMaintenanceMode) {
            this.operationMode = OperationMode.MAINTENANCE;
            cancelAllJobs();
            cancelJobSchedulingConfigurationPollerExecutor();
        } else {
            this.operationMode = OperationMode.NORMAL;
            this.updateJobSchedulingConfigurations();
            createJobSchedulingConfigurationPollerExecutor();
        }
    }

    /**
     * {@link JobListener} interface implementations The JobManager should be aware of Jobs being started, finished etc.
     */
    @Override
    public final String getName() {
        return QUARTZ_JOB_LISTENER_NAME;
    }

    @Override
    public final void jobExecutionVetoed(final JobExecutionContext context) {
        final JobManagerManagedJob currentJob = getManagedJobByJobDetail(context.getJobDetail());
        LOG.error("Job Execution was vetoed. Job: [{}]", currentJob.getJobSchedulingConfig());
    }

    @Override
    public final void jobWasExecuted(final JobExecutionContext context, final JobExecutionException jobException) {
        final JobManagerManagedJob currentJob = getManagedJobByJobDetail(context.getJobDetail());
        if (currentJob != null) {
            final Job quartzJob = context.getJobInstance();
            if (quartzJob != null && RunningWorker.class.isInstance(quartzJob)) {
                currentJob.onFinishRunningWorker((RunningWorker) quartzJob);
            }
        } else {
            if (jobException == null) {
                LOG.warn(
                    "Could not find JobManagerManagedJob entry for: [jobWasExecuted] callback. JobExecutionContext was: [{}]",
                    context);
            } else {
                LOG.warn(
                    "Could not find JobManagerManagedJob entry for: [jobWasExecuted] callback. JobExecutionContext was: [{}]. JobExecutionException was: [{}]",
                    new Object[] {context, jobException.getMessage(), jobException});
            }
        }
    }

    /**
     * {@link Runnable} interface implementation This method contains the refresh/rescheduling functionality.
     */
    @Override
    public final void run() {
        try {
            updateJobSchedulingConfigurations();
        } catch (final JobManagerException e) {
            LOG.error("An error occured trying to update Schedulering Configurations/rescheduling Jobs. Error: [{}].",
                e.getMessage(), e);
        }
    }

    @Override
    public final void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
