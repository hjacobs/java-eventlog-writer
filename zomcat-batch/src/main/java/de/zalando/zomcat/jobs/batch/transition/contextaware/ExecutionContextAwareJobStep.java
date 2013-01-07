package de.zalando.zomcat.jobs.batch.transition.contextaware;

import java.util.Map;

import org.quartz.JobExecutionContext;

/**
 * Marker interface for context aware JobSteps. Implementors of this class have access to both the JobExecutionContext
 * (i.e. mergedJobData from zomcat's quartz job infrastructure) and the (thread-)local job execution context which is
 * made available at every startup of a job (avoiding thus conflicts with other possible running jobs of the same
 * instance, since quartz jobs are singletons.). Usual implementations of Job Steps (Fetcher, Processor, Writer) can
 * simply extend the the convenience implementations of this interface
 * (ExecutionContextAwareItem{Fetcher,Processor,Writer}). IMPORTANT: Jobs that are single classes implementing the
 * required steps' interfaces can do away with this interface since AbstractBulkProcessingJob already provides access to
 * these contexts.
 *
 * @author  jjochens
 */
public interface ExecutionContextAwareJobStep {

    void setJobExecutionContext(JobExecutionContext executionContext);

    void setLocalJobExecutionContext(Map<String, Object> localExecutionContext);
}
