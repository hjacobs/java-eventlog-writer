package de.zalando.zomcat.jobs.management.mbean.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
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

    @ManagedOperation(description = "Trigger the JobManagers config update and respective (re)scheduling of jobs")
    @Override
    public void triggerJobSchedulingConfigurationUpdate() {
        try {
            jobManager.updateJobSchedulingConfigurations();
        } catch (final JobManagerException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @ManagedOperation(description = "Check if a given Job is running")
    @ManagedOperationParameters(
        value = {
            @ManagedOperationParameter(name = "jobDetailName", description = "Quartz JobDetail Name"),
            @ManagedOperationParameter(
                name = "jobDetailGroup", description = "Quartz JobDetail Group - may be null"
            )
        }
    )
    @Override
    public boolean isJobRunning(final String jobDetailName, final String jobDetailGroup) {

        // TODO Auto-generated method stub
        return false;
    }

    @ManagedOperation(description = "Check if a given Job is scheduled")
    @ManagedOperationParameters(
        value = {
            @ManagedOperationParameter(name = "jobDetailName", description = "Quartz JobDetail Name"),
            @ManagedOperationParameter(
                name = "jobDetailGroup", description = "Quartz JobDetail Group - may be null"
            )
        }
    )
    @Override
    public boolean isJobScheduled(final String jobDetailName, final String jobDetailGroup) {

        // TODO Auto-generated method stub
        return false;
    }

    @ManagedOperation(description = "Trigger a given Job by the JobSchedulingConfig name")
    @ManagedOperationParameters(
        value = {
            @ManagedOperationParameter(name = "jobDetailName", description = "Quartz JobDetail Name"),
            @ManagedOperationParameter(
                name = "jobDetailGroup", description = "Quartz JobDetail Group - may be null"
            )
        }
    )
    @Override
    public void triggerJob(final String jobDetailName, final String jobDetailGroup) {
        try {
            jobManager.triggerJob(jobDetailName, jobDetailGroup);
        } catch (final JobManagerException e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
