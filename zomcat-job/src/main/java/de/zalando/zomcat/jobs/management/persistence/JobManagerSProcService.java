package de.zalando.zomcat.jobs.management.persistence;

import java.util.List;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcService;

import de.zalando.zomcat.jobs.management.JobManager;
import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;
import de.zalando.zomcat.jobs.management.persistence.model.DatabaseJobSchedulingConfiguration;

/**
 * SprocService interface for {@link JobManager}. Allows retrieval of {@link JobSchedulingConfiguration} instances via
 * the respective Applications Database
 */
@SProcService
public interface JobManagerSProcService {

    /**
     * Getter for {@link JobSchedulingConfiguration} instances. Do this by searching shards (applications like
     * PartnerService have a ShardedDataSourceProvider of some kind. Search all shards, use first matching result)
     *
     * @return  {@link List} of {@link JobSchedulingConfiguration} provided by database
     */
    @SProcCall(name = "get_job_scheduling_configs", searchShards = true)
    List<DatabaseJobSchedulingConfiguration> getJobSchedulingConfigurations();
}
