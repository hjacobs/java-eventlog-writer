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

    int scheduledJobCount();

    int runningJobCount();

}
