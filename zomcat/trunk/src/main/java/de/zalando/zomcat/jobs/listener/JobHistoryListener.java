package de.zalando.zomcat.jobs.listener;

import java.util.UUID;

import org.apache.log4j.Logger;

import org.joda.time.DateTime;

import org.quartz.JobExecutionContext;

import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import de.zalando.domain.ComponentBean;

import de.zalando.zomcat.flowid.FlowId;
import de.zalando.zomcat.jobs.JobListener;
import de.zalando.zomcat.jobs.RunningWorker;

@Component(JobHistoryListener.JOB_HISTORY_LISTENER)
public class JobHistoryListener implements JobListener, ComponentBean {
    private static final Logger DB_LOG = Logger.getLogger(JobHistoryListener.class.getCanonicalName() + ".db_log");
    private static final Logger LOG = Logger.getLogger(JobHistoryListener.class);

    // The bean name
    static final String JOB_HISTORY_LISTENER = "jobHistoryListener";

    // get the bean name
    public static String beanName() {
        return JOB_HISTORY_LISTENER;
    }

    @Override
    public String getBeanName() {
        return beanName();
    }

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
            final String appInstanceKey) {

        // get full qualified name of this job:
        final String jobName = runningWorker.getClass().getCanonicalName();

        // use the internal start time as startTime:
        final DateTime startTime = runningWorker.getInternalStartTime();

        // create the history entry in log file:
        try {
            String flowId = FlowId.peekFlowId();

            if (Strings.isNullOrEmpty(flowId)) {
                flowId = UUID.randomUUID().toString();
                LOG.warn("job: " + jobName
                        + " is running without flow-id. Please register a valid JobFlowIdListener. Meanwhile this job generated a new unconnected uuid: "
                        + flowId);
            }

            runningWorker.setFlowId(flowId);
            DB_LOG.trace("0," + flowId + "," + jobName + "," + appInstanceKey + "," + startTime + ",");
        } catch (final Throwable e) {

            // make sure to catch everything so that the jobs are not
            // interrupted:
            LOG.fatal("Could not create a job history entry: " + jobName + ", host: " + appInstanceKey + ", startTime: "
                    + startTime);
        }
    }

    @Override
    public void stopRunning(final RunningWorker runningWorker, final Throwable t) {

        // store the stop time for the job and/or any exceptions
        try {
            DB_LOG.trace("1," + runningWorker.getFlowId() + ",,,," + new DateTime(), t);
        } catch (final Throwable e) {

            // make sure to catch everything so that the jobs are not
            // interrupted:
            LOG.fatal("Could not update a job history entry: " + runningWorker.getFlowId() + ", runningWorkerId: "
                    + runningWorker.getId(), e);
        }
    }
}
