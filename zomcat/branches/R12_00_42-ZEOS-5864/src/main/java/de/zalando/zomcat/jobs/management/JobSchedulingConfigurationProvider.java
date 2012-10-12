package de.zalando.zomcat.jobs.management;

import java.util.List;

public interface JobSchedulingConfigurationProvider {

    List<JobSchedulingConfiguration> provideSchedulerConfigs() throws JobSchedulingConfigurationProviderException;
}
