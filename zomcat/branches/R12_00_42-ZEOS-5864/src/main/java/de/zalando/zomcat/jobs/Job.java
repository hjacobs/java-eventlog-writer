package de.zalando.zomcat.jobs;

import org.quartz.JobExecutionContext;

import de.zalando.domain.ComponentBean;

public interface Job extends ComponentBean {

    /**
     * Getter for the {@link JobGroup} this job belongs to.
     *
     * @return  the {@link JobGroup} this job belongs to.
     */
    JobGroup getJobGroup();

    /**
     * Getter for Configuration Source - the {@link JobConfigSource} interface is implemented in ApplicationConfig
     * interfaces.
     *
     * @return  The {@link JobConfigSource} instance to be used by the job to configure itself.
     */
    JobConfigSource getConfigurationSource();

    /**
     * Execution of Business Logic of Job.
     *
     * @param   context {@link JobExecutionContext} instance for Job Run
     * @param   config {@link JobConfig} instance for Job Run
     *
     * @throws  Exception
     */
    void doRun(final JobExecutionContext context, final JobConfig config) throws Exception;
}
