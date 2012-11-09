package de.zalando.zomcat.jobs.management.persistence.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import de.zalando.sprocwrapper.AbstractSProcService;
import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;

import de.zalando.zomcat.jobs.management.persistence.JobManagerSProcService;
import de.zalando.zomcat.jobs.management.persistence.model.DatabaseJobSchedulingConfiguration;

@Service("jobManagerSProcService")
public class JobManagerSProcServiceImpl extends AbstractSProcService<JobManagerSProcService, DataSourceProvider>
    implements JobManagerSProcService {

    @Autowired
    public JobManagerSProcServiceImpl(@Qualifier("jobManagerDatasourceProvider") final DataSourceProvider provider) {
        super(provider, JobManagerSProcService.class);
    }

    @Override
    public List<DatabaseJobSchedulingConfiguration> getJobSchedulingConfigurations() {
        return sproc.getJobSchedulingConfigurations();
    }

}
