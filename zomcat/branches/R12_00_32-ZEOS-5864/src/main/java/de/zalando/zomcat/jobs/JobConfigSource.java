package de.zalando.zomcat.jobs;

import de.zalando.domain.ComponentBean;

import de.zalando.zomcat.configuration.AppInstanceKeySource;
import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;

public interface JobConfigSource extends AppInstanceKeySource {

    /**
     * Fetch JobConfig for given {@link ComponentBean} instance.
     *
     * @param   job  The Job Spring Bean to fetch JobConfig for
     *
     * @return  The JobConfig created for given Job
     */
    JobConfig getJobConfig(Job job);

    /**
     * Get JobConfig for given {@link JobSchedulingConfiguration}.
     *
     * @param   jobSchedulingConfig  The {@link JobSchedulingConfiguration} to get {@link JobConfig} for
     *
     * @return  the {@link JobConfig} matching the {@link JobSchedulingConfiguration}, <code>null</code> otherwise
     */
    JobConfig getJobConfig(JobSchedulingConfiguration jobSchedulingConfig);

}
