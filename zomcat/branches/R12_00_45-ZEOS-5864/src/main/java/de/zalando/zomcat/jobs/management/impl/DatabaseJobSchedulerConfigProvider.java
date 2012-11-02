package de.zalando.zomcat.jobs.management.impl;

import java.sql.SQLException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;
import de.zalando.zomcat.jobs.management.JobSchedulingConfigurationProvider;
import de.zalando.zomcat.jobs.management.JobSchedulingConfigurationProviderException;
import de.zalando.zomcat.jobs.management.persistence.AbstractGetJobSchedulingConfigsProc;
import de.zalando.zomcat.jobs.management.persistence.model.DatabaseJobSchedulingConfiguration;

/**
 * Database Job Scheduler Config Provider.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public class DatabaseJobSchedulerConfigProvider extends AbstractJobSchedulerConfigProvider
    implements JobSchedulingConfigurationProvider {

    private static final transient Logger LOG = LoggerFactory.getLogger(DatabaseJobSchedulerConfigProvider.class);

    @Autowired
    private AbstractGetJobSchedulingConfigsProc getJobSchedulingConfigsProc;

    @Override
    public List<JobSchedulingConfiguration> provideSchedulerConfigs()
        throws JobSchedulingConfigurationProviderException {
        LOG.debug("Fetching JobSchedulingConfigurations from Database");
        Preconditions.checkArgument(getJobSchedulingConfigsProc != null, "getJobSchedulingConfigsProc must be set.");
        try {
            final List<JobSchedulingConfiguration> retVal = Lists.newArrayList();
            final List<DatabaseJobSchedulingConfiguration> dbConfigs =
                getJobSchedulingConfigsProc.getJobSchedulingConfigurations();
            for (final DatabaseJobSchedulingConfiguration curJobConfig : dbConfigs) {
                retVal.add(curJobConfig.toJobSchedulingConfiguration());
            }

            return retVal;
        } catch (final SQLException e) {
            throw new JobSchedulingConfigurationProviderException(e);
        }
    }
}
