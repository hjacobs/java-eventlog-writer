package de.zalando.zomcat.jobs.batch.transition.contextaware;

import java.util.Map;

import org.quartz.JobExecutionContext;

/**
 * Convenience implementation of an ExecutionContextAware ItemProcessor.
 *
 * @param   <Item>
 *
 * @author  jjochens
 */
public abstract class BaseExecutionContextAwareJobStep implements ExecutionContextAwareJobStep {

    private JobExecutionContext jobExecutionContext;
    private ThreadLocal<Map<String, Object>> localJobExecutionContext;

    @Override
    public void setJobExecutionContext(final JobExecutionContext jobExecutionContext) {
        this.jobExecutionContext = jobExecutionContext;

    }

    @Override
    public void setLocalJobExecutionContext(final ThreadLocal<Map<String, Object>> localJobExecutionContext) {
        this.localJobExecutionContext = localJobExecutionContext;

    }

    public JobExecutionContext getJobExecutionContext() {
        return jobExecutionContext;
    }

    public Map<String, Object> getLocalJobExecutionContext() {
        return localJobExecutionContext.get();
    }

}
