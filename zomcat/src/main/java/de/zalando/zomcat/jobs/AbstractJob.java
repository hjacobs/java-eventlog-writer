package de.zalando.zomcat.jobs;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import org.joda.time.DateTime;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import org.springframework.context.ApplicationContext;

import org.springframework.scheduling.quartz.QuartzJobBean;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import de.zalando.zomcat.OperationMode;
import de.zalando.zomcat.jobs.listener.JobFlowIdListener;
import de.zalando.zomcat.jobs.listener.JobHistoryListener;
import de.zalando.zomcat.jobs.listener.StopWatchListener;

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
    private static volatile AtomicInteger globalId = new AtomicInteger(0);

    // the list of job listeners
    private final List<JobListener> jobListeners = Lists.newArrayList();
    private final DateTime startTime;

    private final int id;

    private ApplicationContext applicationContext = null;
    private JobsStatusBean jobsStatusBean = null;

    private String jobHistoryId = null;
    private String appInstanceKey = null;

    private final StopWatchListener stopWatchListener = new StopWatchListener();

    /**
     * Default Constructor.
     */
    protected AbstractJob() {
        super();
        startTime = new DateTime();
        id = globalId.incrementAndGet();
    }

    /**
     * A protected registerListener operation to allow to register the listeners after the spring context has been
     * setup.
     */
    protected void registerListener() {
        addJobListener(new JobFlowIdListener());
        addJobListener(new JobHistoryListener());
        addJobListener(new QuartzJobInfoBeanListener());
        addJobListener(stopWatchListener);
    }

    /**
     * Entry point for Quartz.
     */
    @Override
    protected final void executeInternal(final JobExecutionContext context) {

        // no job should be allowed without description
        Preconditions.checkArgument(!isNullOrEmpty(getDescription()),
            "Aborting Job: no description for job defined: " + getBeanName());

        final JobConfig config = getJobConfig();
        if (shouldRun(config)) {

            // register all listeners (if not already done)
            doRegisterListener();

            // notify about start running this job
            notifyStartRunning(context);

            // run the job and catch ALL exceptions
            // TBC define a condition to control job execution in local environment
            // (e.g. job.runlocal=name1 name2 ....)

            Throwable throwable = null;
            try {
                doRun(context, config);
            } catch (final Throwable e) {
                log(Level.ERROR, "failed to run job: " + e.getMessage(), e);
                throwable = e;
            } finally {

                // notify about stop running this job
                notifyStopRunning(throwable);
            }
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
     * @see  de.zalando.commons.backend.domain.monitoring.RunningWorker#getId()
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.RunningWorker#getActualProcessedItemNumber()
     */
    @Override
    public Integer getActualProcessedItemNumber() {
        return null;
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.RunningWorker#getStartTime()
     */
    @Override
    public DateTime getStartTime() {
        return startTime;
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.RunningWorker#getTotalNumberOfItemsToBeProcessed()
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
    public String getJobHistoryId() {
        if (jobHistoryId == null) {
            return String.valueOf(id);
        }

        return jobHistoryId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setJobHistoryId(final String jobHistoryId) {
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
        this.jobListeners.add(jobListener);
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

        // check for disabled job by mbean
        if (isJobDisabled()) {

            // get the correct logger and debug
            log(Level.INFO, "job is disabled and will not start", null);
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

    @Override
    public JobConfigSource getConfigurationSource() {
        return getApplicationContext().getBean(JobConfigSource.class);
    }

    @Override
    public JobConfig getJobConfig() {
        return getConfigurationSource().getJobConfig(this);
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

    /**
     * call the registerListener operation (once).
     */
    private void doRegisterListener() {

        // if there are already listeners registered, ignore the call
        if (jobListeners.isEmpty()) {
            registerListener();
        }
    }

    private void log(final Priority priority, final String message, final Throwable throwable) {
        Logger.getLogger(getClass()).log(priority, message, throwable);
    }

    /**
     * Inner {@link JobListener} implementation for notifying the JobsStatusBean about the start/stop of the current
     * {@link RunningWorker}.
     *
     * @author  wolters
     */
    class QuartzJobInfoBeanListener implements JobListener {

        @Override
        public void startRunning(final RunningWorker runningWorker, final JobExecutionContext context,
                final String host) {
            QuartzJobInfoBean quartzJobInfoBean = null;

            try {
                final JobDataMap mergedJobDataMap = context.getMergedJobDataMap();

                // check if there is a filled merged job data map, these are not
                // supported yet
                // => don't fill quartzJobInfoBean so it can not be triggered
                // instantly later
                if ((mergedJobDataMap == null) || mergedJobDataMap.isEmpty()) {
                    quartzJobInfoBean = new QuartzJobInfoBean(context.getScheduler().getSchedulerName(),
                            context.getJobDetail().getName(), context.getJobDetail().getGroup());
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
