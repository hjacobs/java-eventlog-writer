package de.zalando.zomcat.jobs;

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

import com.google.common.collect.Lists;

import de.zalando.zomcat.OperationMode;
import de.zalando.zomcat.SystemConstants;

public abstract class AbstractJob extends QuartzJobBean implements RunningWorker {

    private static final Logger LOG = Logger.getLogger(AbstractJob.class);
    private static volatile AtomicInteger globalId = new AtomicInteger(0);
    private final int id;
    private final DateTime startTime;
    private DateTime internalStartTime;
    private ApplicationContext applicationContext = null;
    private JobsStatusBean jobsStatusBean = null;

    private String jobHistoryId = null;
    private String appInstanceKey = null;

    // the list of job listeners
    private final List<JobListener> jobListeners = Lists.newArrayList();

    protected AbstractJob() {
        super();

        id = globalId.incrementAndGet();
        startTime = new DateTime();

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("globalId [%s] id [%d]", globalId.toString(), id));
        }
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

    @Override
    protected void executeInternal(final JobExecutionContext context) {

        // check for maintenance mode
        if (isMaintenanceMode()) {

            // get the correct logger and debug
            log(Level.INFO, "maintenance mode, job will not start", null);
            return;
        }

        // check for disabled job
        if (isJobDisabled()) {

            // get the correct logger and debug
            log(Level.INFO, "job is disabled and will not start", null);
            return;
        }

        // register all listeners (if not already done)
        doRegisterListener();

        // set the internalStartTime
        internalStartTime = new DateTime();

        // notify about start running this job
        notifyStartRunning(context);

        // run the job and catch ALL exceptions
        // TBC define a condition to control job execution in local environment
        // (e.g. job.runlocal=name1 name2 ....)

        Throwable throwable = null;
        try {
            doRun(context);
        } catch (final Exception e) {
            log(Level.ERROR, "failed to run job", e);
            throwable = e;
        }

        // notify about stop running this job
        notifyStopRunning(throwable);
    }

    /**
     * implement this method with the code which should be executed in each job run! care about catching ALL exceptions
     *
     * @param   the  <code>JobExecutionContext</code>
     *
     * @throws  Exception  if any unanticipated/unhandled error occurs
     */
    protected abstract void doRun(JobExecutionContext context) throws Exception;

    /**
     * Get the current host name. The derived class should overwrite this implementation and deliver a valid host name.
     * The fallback solution is to extract the appInstanceKey from the system environment.
     *
     * @return  the current host name (mostly appInstanceKey)
     */
    protected String getHost() {
        if (appInstanceKey == null) {
            appInstanceKey = System.getProperty(SystemConstants.SYSTEM_PROPERTY_APP_INSTANCE_KEY);
            if (appInstanceKey == null) {
                appInstanceKey = "NO_VALID_HOST_IMPLEMENTATION_FOUND_IMPLEMENT_IN_DERIVED_CLASS";
            }
        }

        return appInstanceKey;
    }

    /**
     * A protected registerListener operation to allow to register the listeners after the spring context has been
     * setup.
     */
    protected void registerListener() {
        addJobListener(new QuartzJobInfoBeanListener());
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

    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    protected void addJobListener(final JobListener jobListener) {
        this.jobListeners.add(jobListener);
    }

    protected JobsStatusBean getJobsStatusBean() {
        if (jobsStatusBean == null) {
            jobsStatusBean = (JobsStatusBean) getApplicationContext().getBean("jobsStatusBean");
        }

        return jobsStatusBean;
    }

    /**
     * notify all {@link JobListener} that this job starts running.
     */
    protected void notifyStartRunning(final JobExecutionContext context) {
        for (final JobListener jobListener : jobListeners) {
            try {
                jobListener.startRunning(this, context, getHost());
            } catch (final Throwable t) {

                // log the error and proceed
                LOG.fatal("Could not execute startRunning on jobListener: " + jobListener + ", context:" + context, t);
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
                LOG.fatal("Could not execute stopRunning on jobListener: " + jobListener, t);
            }
        }
    }

    /**
     * @return  the internalStartTime
     */
    @Override
    public DateTime getInternalStartTime() {
        return internalStartTime;
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
     * {@inheritDoc}
     */
    @Override
    public String getJobHistoryId() {
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
     * @see  de.zalando.commons.backend.domain.monitoring.RunningWorker#getDescription()
     */
    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final AbstractJob other = (AbstractJob) obj;
        if (id != other.getId()) {
            return false;
        }

        return true;
    }

    protected void log(final Priority priority, final String message, final Throwable throwable) {
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
