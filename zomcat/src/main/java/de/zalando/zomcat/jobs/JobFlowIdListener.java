package de.zalando.zomcat.jobs;

import org.apache.log4j.Logger;

import org.quartz.JobExecutionContext;

import org.springframework.stereotype.Component;

import de.zalando.domain.ComponentBean;

import de.zalando.zomcat.flowid.FlowId;

@Component(JobFlowIdListener.JOB_FLOW_ID_LISTENER)
public class JobFlowIdListener implements JobListener, ComponentBean {
    private static final Logger LOG = Logger.getLogger(JobFlowIdListener.class);

    // The bean name
    static final String JOB_FLOW_ID_LISTENER = "jobFlowIdListener";

    // get the bean name
    public static String beanName() {
        return JOB_FLOW_ID_LISTENER;
    }

    @Override
    public String getBeanName() {
        return beanName();
    }

    @Override
    public void startRunning(final RunningWorker runningWorker, final JobExecutionContext context, final String host) {

        // clear this thread: there can be no other context in there...
        FlowId.clear();
        FlowId.generateAndPushFlowId();
        if (LOG.isDebugEnabled()) {
            LOG.debug("start running job with flowId: " + FlowId.peekFlowId());
        }
    }

    @Override
    public void stopRunning(final RunningWorker runningWorker, final Throwable t) {
        final String flowId = FlowId.popFlowId();
        if (LOG.isDebugEnabled()) {
            LOG.debug("stop running job with flowId: " + flowId);
        }
    }
}
