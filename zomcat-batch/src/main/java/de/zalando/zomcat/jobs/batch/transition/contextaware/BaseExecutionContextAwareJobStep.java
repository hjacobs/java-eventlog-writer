package de.zalando.zomcat.jobs.batch.transition.contextaware;

import java.util.Map;

import org.quartz.JobExecutionContext;

/**
 * Convenience implementation of an ExecutionContextAware ItemProcessor. Important observations : - Any Fetcher,
 * Processor or Writer that is ContextAware MAY NOT be a Spring Singleton, or else the local contexts may get messed up
 * (they are not thread safe). Simple way to meet the requirement is to define scope = "prototype" for the bean.
 *
 * @param   <Item>
 *
 * @author  jjochens
 */
public abstract class BaseExecutionContextAwareJobStep implements ExecutionContextAwareJobStep {

    private JobExecutionContext jobExecutionContext;
    private Map<String, Object> localJobExecutionContext;

    @Override
    public void setJobExecutionContext(final JobExecutionContext jobExecutionContext) {
        this.jobExecutionContext = jobExecutionContext;

    }

    @Override
    public void setLocalJobExecutionContext(final Map<String, Object> localJobExecutionContext) {
        this.localJobExecutionContext = localJobExecutionContext;

    }

    public JobExecutionContext getJobExecutionContext() {
        return jobExecutionContext;
    }

    public Map<String, Object> getLocalJobExecutionContext() {
        return localJobExecutionContext;
    }

}
