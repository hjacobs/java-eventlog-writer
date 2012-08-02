package de.zalando.zomcat.jobs.management.mbean;

import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.JobGroupConfig;
import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;

/**
 * Simple MBean Interface for monitoring/controlling JobManager via JMX.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public interface JobManagerMBean {

    /**
     * Trigger the {@link JobSchedulingConfiguration} update.
     */
    void triggerJobSchedulingConfigurationUpdate();

    /**
     * Checks whether or not a particular job is currently running.
     *
     * @param   jobDetailName   The Quartz JobDetail name - the Jobs camelcase Simple ClassName + the Trigger Sequence
     *                          Number
     * @param   jobDetailGroup  The Quartz JobDetail group - can be <code>null</code> when {@link JobGroupConfig} is not
     *                          available on the respective {@link JobConfig}. Otherwise the {@link JobGroupConfig}s
     *                          JobGroupName
     *
     * @return  <code>true</code> if the job is currently running, <code>false</code> otherwise
     */
    boolean isJobRunning(final String jobDetailName, final String jobDetailGroup);

    /**
     * Checks whether or not a particular job is currently scheduled to run at some point in the future.
     *
     * @param   jobDetailName   The Quartz JobDetail name - the Jobs camelcase Simple ClassName + the Trigger Sequence
     *                          Number
     * @param   jobDetailGroup  The Quartz JobDetail group - can be <code>null</code> when {@link JobGroupConfig} is not
     *                          available on the respective {@link JobConfig}. Otherwise the {@link JobGroupConfig}s
     *                          JobGroupName
     *
     * @return  <code>true</code> if the job is scheduled to run, <code>false</code> otherwise
     */
    boolean isJobScheduled(final String jobDetailName, final String jobDetailGroup);

    /**
     * Trigger a dedicated Job.
     *
     * @param  jobDetailName   The Quartz JobDetail name - the Jobs camelcase Simple ClassName + the Trigger Sequence
     *                         Number
     * @param  jobDetailGroup  The Quartz JobDetail group - can be <code>null</code> when {@link JobGroupConfig} is not
     *                         available on the respective {@link JobConfig}. Otherwise the {@link JobGroupConfig}s
     *                         JobGroupName
     */
    void triggerJob(final String jobDetailName, final String jobDetailGroup);

}
