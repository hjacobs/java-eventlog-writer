package de.zalando.zomcat.jobs.management.mbean.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jmx.export.annotation.ManagedResource;

import org.springframework.stereotype.Component;

import de.zalando.zomcat.jobs.management.JobManager;
import de.zalando.zomcat.jobs.management.JobManagerException;
import de.zalando.zomcat.jobs.management.mbean.JobManagerMBean;

@ManagedResource(objectName = "Zalando:name=Job Manager")
@Component("jobManagerMBean")
public class JobManagerMBeanImpl implements JobManagerMBean {

    private static final transient Logger LOG = LoggerFactory.getLogger(JobManagerMBeanImpl.class);

    @Autowired
    private JobManager jobManager;

    @Override
    public void triggerJobSchedulingConfigurationUpdate() {
        try {
            jobManager.updateJobSchedulingConfigurations();
        } catch (final JobManagerException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public int scheduledJobCount() {

        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int runningJobCount() {

        // TODO Auto-generated method stub
        return 0;
    }

}
