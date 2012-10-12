package de.zalando.zomcat.jobs.management;

import java.util.List;

import com.google.common.collect.Lists;

import de.zalando.zomcat.jobs.management.impl.AbstractJobSchedulerConfigProvider;

/**
 * Test {@link JobSchedulingConfigurationProvider} for testing the JobManager.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public class TestJobSchedulingConfigProvider extends AbstractJobSchedulerConfigProvider {

    private List<JobSchedulingConfiguration> configurationsToProvide;

    @Override
    public List<JobSchedulingConfiguration> provideSchedulerConfigs()
        throws JobSchedulingConfigurationProviderException {

        if (configurationsToProvide == null) {
            configurationsToProvide = Lists.newArrayList();
        }

        return configurationsToProvide;
    }

    /**
     * Set Configurations to provide.
     *
     * @param  configurationsToProvide
     */
    public void setConfigurationsToProvide(final List<JobSchedulingConfiguration> configurationsToProvide) {
        this.configurationsToProvide = configurationsToProvide;
    }
}
