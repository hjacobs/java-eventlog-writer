package de.zalando.zomcat.jobs;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.joda.time.DateTime;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import org.springframework.context.ApplicationContext;

import org.springframework.scheduling.quartz.QuartzJobBean;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

import de.zalando.zomcat.HostStatus;
import de.zalando.zomcat.OperationMode;
import de.zalando.zomcat.flowid.FlowId;
import de.zalando.zomcat.jobs.listener.JobFlowIdListener;
import de.zalando.zomcat.jobs.listener.JobHistoryListener;
import de.zalando.zomcat.jobs.listener.StopWatchListener;
import de.zalando.zomcat.jobs.lock.LockResourceManager;

/**
 * Abstract Job.
 *
 * @author  carsten.wolters
 */
public abstract class AbstractJob extends QuartzJobBean implements Job, RunningWorker {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(AbstractJob.class);

    private static final long DEFAULT_EXPECTED_MAXIMUM_DURATION = 60000; // 1 min.

    private static volatile AtomicInteger globalId = new AtomicInteger(0);

    private static final Pattern LOCK_RESOURCE_NAME_PATTERN = Pattern.compile("^[A-Z][A-Z_]*[A-Z]$");

    // the list of job listeners
    private final List<JobListener> jobListeners = Lists.newArrayList();
    private final DateTime startTime;

    private final int id;

    private ApplicationContext applicationContext = null;
    private JobsStatusBean jobsStatusBean = null;

    private String jobHistoryId = null;
    private String appInstanceKey = null;

    private JobConfig jobConfig = null;
    private JobGroupConfig jobGroupConfig = null;

    private final StopWatchListener stopWatchListener = new StopWatchListener();

    private LockResourceManager lockResourceManager;

    protected JobExecutionContext executionContext;

    /**
     * Default Constructor.
     */
    protected AbstractJob() {
        super();
        startTime = new DateTime();
        id = globalId.incrementAndGet();

    }

    /**
     * expose job data map (properties in scheduler.conf) for fetchers/writers if they are defined in the same class.
     *
     * @return
     */
    protected JobDataMap getJobDataMap() {
        return executionContext.getMergedJobDataMap();
    }

    /**
     * A protected registerListener operation to allow to register the listeners after the spring context has been
     * setup.
     */
    protected void registerListener() {
        addJobListener(new JobFlowIdListener());
        addJobListener(new JobHistoryListener());
        if (!isJobManagerProvidedJobConfig()) {
            addJobListener(new QuartzJobInfoBeanListener());
        }

        addJobListener(stopWatchListener);
    }

    protected void setupLockResourceManager() {

        if (getLockResource() == null) {
            return;
        }

        LockResourceManager bean = null;

        // under tests and on some components spring may not be available.
        if (applicationContext == null) {
            LOG.warn("Application context not available. Starting instance without resource locking support.");
        } else {

            // workaround try to use redis implementation if the bean is available
            if (applicationContext.containsBean("redisLockResourceManager")) {
                bean = applicationContext.getBean("redisLockResourceManager", LockResourceManager.class);
            }

            if (bean == null) {
                try {
                    bean = applicationContext.getBean("lockResourceManager", LockResourceManager.class);
                } catch (final NoSuchBeanDefinitionException e) {
                    LOG.info("Starting instance without resource locking support.", e);
                }
            }
        }

        lockResourceManager = bean;
    }

    /**
     * Entry point for Quartz.
     */
    @Override
    protected final void executeInternal(final JobExecutionContext context) {

        executionContext = context;

        // register all listeners (if not already done)
        registerListener();
        setupLockResourceManager();

        if (!isJobManagerProvidedJobConfig()) {

            // no job should be allowed without description
            checkArgument(!isNullOrEmpty(getDescription()),
                "Aborting Job: no description for job defined: " + getBeanName());
        }

        final JobConfig config = getJobConfig();

        // If no JobConfig was set by JobManager and the Job is not allowed to run, return here
        if (!isJobManagerProvidedJobConfig() && !shouldRun(config)) {
            return;
        }

        final boolean isLockedJob = (lockResourceManager != null && getLockResource() != null);

        notifyExecutionSetUp(context);

        try {

            final String flowId = FlowId.peekFlowId();
            if (isLockedJob) {
                final boolean acquiredLock = lockResourceManager.acquireLock(getBeanName(), getLockResource(), flowId,
                        getExpectedMaximumDuration());

                // if some problem happens from now on and before the lock is released (JVM crash, etc...) the lock
                // needs
                // to be removed manually

                // if we got at this point, we have a lock - else we got an exception that can be ignored.
                if (!acquiredLock) {
                    log(Level.INFO, "job's resource is locked and job will not start", null);
                    return;
                }

                log(Level.DEBUG, "acquired resource lock for job; job will start", null);
            }

            // notify about start running this job
            notifyStartRunning(context);

            // run the job and catch ALL exceptions
            // TBC define a condition to control job execution in local
            // environment
            // (e.g. job.runlocal=name1 name2 ....)

            Throwable throwable = null;
            try {
                doRun(context, config);
            } catch (final Throwable e) {
                log(Level.ERROR, "failed to run job: " + e.getMessage(), e);
                throwable = e;
            } finally {
                try {

                    // notify about stop running this job
                    if (isLockedJob) {

                        // if the database is not available, the lock needs to be removed manually.
                        lockResourceManager.releaseLock(getLockResource(), flowId);
                    }
                } finally {

                    // if an error occurs while releasing the lock, don't forget to notify stop running.
                    notifyStopRunning(throwable);
                }
            }
        } finally {
            notifyExecutionTearDown();
        }
    }

    /**
     * @return  the internalStartTime
     */
    @Override
    public DateTime getInternalStartTime() {
        return stopWatchListener.getStartTime();
    }

    /**
     * @see  RunningWorker#getId()
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * @see  RunningWorker#getActualProcessedItemNumber()
     */
    @Override
    public Integer getActualProcessedItemNumber() {
        return null;
    }

    /**
     * @see  RunningWorker#getStartTime()
     */
    @Override
    public DateTime getStartTime() {
        return startTime;
    }

    /**
     * @see  RunningWorker#getTotalNumberOfItemsToBeProcessed()
     */
    @Override
    public Integer getTotalNumberOfItemsToBeProcessed() {
        return null;
    }

    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFlowId() {
        if (jobHistoryId == null) {
            return String.valueOf(id);
        }

        return jobHistoryId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFlowId(final String jobHistoryId) {
        this.jobHistoryId = jobHistoryId;
    }

    /**
     * Returns the limit dependent on startup parameters. If the service is started a smaller limit should be returned,
     * otherwise the normal limit.
     *
     * @param   config
     *
     * @return
     */
    protected int getLimit(final JobConfig config) {
        return isMaintenanceMode() ? config.getStartupLimit() : config.getLimit();
    }

    protected void addJobListener(final JobListener jobListener) {
        jobListeners.add(jobListener);
    }

    /**
     * notify all {@link JobListener} that this job is being setup.
     */
    protected void notifyExecutionSetUp(final JobExecutionContext context) {
        for (final JobListener jobListener : jobListeners) {
            try {
                jobListener.onExecutionSetUp(this, context, getAppInstanceKey());
            } catch (final Throwable t) {

                // log the error and proceed
                log(Level.FATAL,
                    "Could not execute onExecutionSetUp on jobListener: " + jobListener + ", context:" + context, t);
            }
        }
    }

    /**
     * notify all {@link JobListener} about job tear down.
     */
    protected void notifyExecutionTearDown() {
        for (final JobListener jobListener : Lists.reverse(jobListeners)) {
            try {
                jobListener.onExecutionTearDown(this);
            } catch (final Throwable t) {

                // log the error and proceed
                log(Level.FATAL, "Could not execute onExecutionTearDown on jobListener: " + jobListener, t);
            }
        }
    }

    /**
     * notify all {@link JobListener} that this job starts running.
     */
    protected void notifyStartRunning(final JobExecutionContext context) {
        for (final JobListener jobListener : jobListeners) {
            try {
                jobListener.startRunning(this, context, getAppInstanceKey());
            } catch (final Throwable t) {

                // log the error and proceed
                log(Level.FATAL,
                    "Could not execute startRunning on jobListener: " + jobListener + ", context:" + context, t);
            }
        }
    }

    /**
     * notify all {@link JobListener} that this job has been stopped.
     */
    protected void notifyStopRunning(final Throwable throwable) {
        for (final JobListener jobListener : Lists.reverse(jobListeners)) {
            try {
                jobListener.stopRunning(this, throwable);
            } catch (final Throwable t) {

                // log the error and proceed
                log(Level.FATAL, "Could not execute stopRunning on jobListener: " + jobListener, t);
            }
        }
    }

    /**
     * @return  flag if maintenance mode is turned on or not
     */
    protected boolean isMaintenanceMode() {
        return OperationMode.MAINTENANCE == getJobsStatusBean().getOperationModeAsEnum();
    }

    protected boolean isJobDisabled() {
        final JobTypeStatusBean statusBean = getJobsStatusBean().getJobTypeStatusBean(getClass());
        if (statusBean == null) {
            return false;
        } else {
            return statusBean.isDisabled();
        }
    }

    protected boolean isJobGroupDisabled() {
        if (jobGroupConfig == null) {
            final JobGroupTypeStatusBean statusBean = getJobsStatusBean().getJobGroupTypeStatusBean(getJobConfig());

            if (statusBean == null) {
                return false;
            } else {
                return statusBean.isDisabled();
            }
        } else {
            return !jobGroupConfig.isJobGroupActive();
        }
    }

    /**
     * This method decides, whether the job should run or not. The default implementation only checks that the current
     * appInstanceKey is in the list of allowed appInstanceKeys.
     *
     * @param   appInstanceKey
     * @param   config
     *
     * @return
     */
    @VisibleForTesting
    private boolean shouldRun(final JobConfig config) {

        // check for maintenance mode
        if (isMaintenanceMode()) {

            // get the correct logger and debug
            log(Level.INFO, "maintenance mode, job will not start", null);
            return false;
        }

        if (!HostStatus.isAllocated()) {

            // PF-188 make sure that all jobs are disabled if the host-status is not production ready
            log(Level.INFO, "host status is not ALLOCATED, job will not start", null);
            return false;
        }

        // check for disabled job by mbean
        if (isJobDisabled()) {

            // get the correct logger and debug
            log(Level.INFO, "job is disabled and will not start", null);
            return false;
        }

        // check for disabled job group by mbean
        if (isJobGroupDisabled()) {

            // get the correct logger and debug
            log(Level.INFO, "job group is disabled and will not start", null);
            return false;
        }

        if (config == null) {
            LOG.fatal(String.format(
                    "JobConfig for ComponentBean (Job): %s could not be retrieved from Application Config.",
                    this.getBeanName()));
            return false;
        }

        // do we have a matching appInstanceKey ?
        final boolean hasAllowedInstanceKey = config.isAllowedAppInstanceKey(getAppInstanceKey());
        final boolean isActive = config.isActive();
        final boolean isGroupActive = config.getJobGroupConfig() == null
                || config.getJobGroupConfig().isJobGroupActive();

        // Run Job Only if Job is active AND JobGroup is active
        final boolean shouldRun = hasAllowedInstanceKey && isActive && isGroupActive;
        if (!shouldRun && LOG.isTraceEnabled()) {
            LOG.trace("Job: " + this.getBeanName() + " has been deactivated: hasAllowedInstanceKey = "
                    + hasAllowedInstanceKey + ", isActive: " + isActive + ", isGroupActive: " + isGroupActive
                    + ", shouldRun: " + shouldRun);
        }

        return shouldRun;
    }

    private boolean isJobResourceLocked(final String resource) {
        if (resource == null) {
            return false;
        }

        checkArgument(LOCK_RESOURCE_NAME_PATTERN.matcher(resource).matches(),
            "lock resource name does not follow naming conventions: %s", resource);

        if (lockResourceManager == null) {
            LOG.error(String.format(
                    "No bean lockResourceManager bean is defined but job [%s] is trying to lock resource [%s]!!! Check the component context.",
                    resource));
            throw new IllegalStateException(String.format("LockResourceManager not defined! Can't acquire lock for %s.",
                    resource));
        }

        return lockResourceManager.peekLock(resource);

    }

    /**
     * Identifies what resource (if any) is to be locked. Can be dynamically build using the current job execution
     * context (getJobDataMap())
     *
     * @return  some string identifying the resource to lock on or null if no locking required
     */
    protected String getLockResource() {
        return null;
    }

    /**
     * Gets the expected maximum duration.
     *
     * <p>Default: 60000 milliseconds
     *
     * @return  the expected maximum duration in milliseconds
     */
    protected long getExpectedMaximumDuration() {
        return DEFAULT_EXPECTED_MAXIMUM_DURATION;
    }

    @Override
    public JobConfigSource getConfigurationSource() {
        return getApplicationContext().getBean(JobConfigSource.class);
    }

    @Override
    public JobConfig getJobConfig() {

        // If Set it was set by JobManager
        if (jobConfig != null) {
            return jobConfig;
        }

        // Otherwise consult the Configuration Source
        return getConfigurationSource().getJobConfig(this);
    }

    public void setJobConfig(final JobConfig jobConfig) {
        this.jobConfig = jobConfig;
    }

    public void setJobGroupConfig(final JobGroupConfig jobGroupConfig) {
        this.jobGroupConfig = jobGroupConfig;
    }

    /**
     * Get the current host name. The derived class should overwrite this implementation and deliver a valid host name.
     * The fallback solution is to extract the appInstanceKey from the system environment.
     *
     * @return  the current host name (mostly appInstanceKey)
     */
    protected String getAppInstanceKey() {
        if (appInstanceKey == null) {
            appInstanceKey = getConfigurationSource().getAppInstanceKey();
        }

        return appInstanceKey;
    }

    private JobsStatusBean getJobsStatusBean() {
        if (jobsStatusBean == null) {
            jobsStatusBean = (JobsStatusBean) getApplicationContext().getBean("jobsStatusBean");
        }

        return jobsStatusBean;
    }

    private void log(final Priority priority, final String message, final Throwable throwable) {
        Logger.getLogger(getClass()).log(priority, message, throwable);
    }

    private boolean isJobManagerProvidedJobConfig() {
        return jobConfig != null;
    }

    /**
     * default implementation to return null for job group, override in actual job class to assign the job to a group.
     *
     * @return
     */
    @Override
    public JobGroup getJobGroup() {
        return null;
    }

    /**
     * default implementation for job name: return class name with first char lowercased
     *
     * @return
     */
    @Override
    public String getBeanName() {
        final String className = getClass().getSimpleName();
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    @Override
    public Long getThreadCPUNanoSeconds() {
        return stopWatchListener.getThreadCPUNanoSeconds();
    }

    /**
     * Inner {@link JobListener} implementation for notifying the JobsStatusBean about the start/stop of the current
     * {@link RunningWorker}.
     *
     * @author  wolters
     */
    class QuartzJobInfoBeanListener implements JobListener {

        @Override
        public void onExecutionSetUp(final RunningWorker runningWorker, final JobExecutionContext context,
                final String appInstanceKey) {
            // nothing to do on this stage
        }

        @Override
        public void onExecutionTearDown(final RunningWorker runningWorker) {
            // nothing to do on this stage
        }

        @Override
        public void startRunning(final RunningWorker runningWorker, final JobExecutionContext context,
                final String host) {
            final JobsStatusBean jobsStatusBean = getJobsStatusBean();
            QuartzJobInfoBean quartzJobInfoBean = jobsStatusBean.getJobTypeStatusBean(runningWorker.getClass())
                                                                .getQuartzJobInfoBean();

            try {
                final JobDataMap mergedJobDataMap = context.getMergedJobDataMap();

                // check if there is a filled merged job data map, these are not
                // supported yet
                // => don't fill quartzJobInfoBean so it can not be triggered
                // instantly later
                if (quartzJobInfoBean == null) {
                    quartzJobInfoBean = new QuartzJobInfoBean(context.getScheduler().getSchedulerName(),
                            context.getJobDetail().getName(), context.getJobDetail().getGroup(), mergedJobDataMap);
                } else {
                    quartzJobInfoBean.setJobDataMap(mergedJobDataMap);
                }
            } catch (final Exception e) {
                LOG.error("failed to create quartz job info, perhaps it can not be triggered again manually", e);
            }

            getJobsStatusBean().incrementRunningWorker(runningWorker, quartzJobInfoBean);

        }

        @Override
        public void stopRunning(final RunningWorker runningWorker, final Throwable t) {
            getJobsStatusBean().decrementRunningWorker(runningWorker);
        }
    }
}
