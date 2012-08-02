package de.zalando.zomcat.jobs.management.mbean;

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

    boolean isJobRunning(final String jobDetailName, final String jobDetailGroup);

    boolean isJobScheduled(final String jobDetailName, final String jobDetailGroup);

    /**
     * @param  jobSchedulingConfigName
     */
    void triggerJob(final String jobDetailName, final String jobDetailGroup);

}
