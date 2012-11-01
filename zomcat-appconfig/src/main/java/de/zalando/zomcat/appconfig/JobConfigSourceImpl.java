package de.zalando.zomcat.appconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import de.zalando.appconfig.Configuration;

import de.zalando.zomcat.configuration.AppInstanceKeySource;
import de.zalando.zomcat.jobs.Job;
import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.JobConfigSource;
import de.zalando.zomcat.jobs.JobGroupConfig;

/**
 * JobConfigSource Implementation.
 *
 * @author  Thomas Zirke
 * @author  Carsten Wolters
 */
public abstract class JobConfigSourceImpl implements JobConfigSource, AppInstanceKeySource {

    private static final Logger LOG = LoggerFactory.getLogger(JobConfigSourceImpl.class);

    public abstract Configuration getConfig();

    @Override
    public final JobConfig getJobConfig(final Job job) {

        final Configuration configuration = Preconditions.checkNotNull(getConfig(),
                "configuration cannot be null. Please provide a complete implementation of Configuration.");

        boolean jobsGlobalActive = true;

        // By Default the JobGroup the Job belongs to (even if the Job has no JobGroup) is active
        boolean groupActive = true;

        // By Default the Job is deactivated itself (safety precaution)
        boolean jobActive = false;

        // Get Job Bean Name which is used to create JobConfig Property Names
        final String jobName = job.getBeanName();

        LOG.trace("Attempting to create JobConfig for Job {}", jobName);

        jobsGlobalActive = configuration.getBooleanConfig("jobConfig.global.active", null, true);
        if (!jobsGlobalActive) {
            LOG.info("Jobs are globally disabled (set jobConfig.global.active=true to enable again)");
        }

        // Load the JobGroup for given Job
        String jobGroupName = null;

        if (job.getJobGroup() != null) {
            jobGroupName = job.getJobGroup().groupName();
        } else {
            jobGroupName = configuration.getStringConfig(String.format("jobConfig.%s.jobGroup", jobName), null, null);
        }

        // If there is a Job Group defined on Job Config, fetch the JobGroup Active Status
        JobGroupConfig jobGroupConfig = null;
        if (jobGroupName != null) {
            LOG.trace("Attempting to fetch create JobGroupConfig for JobGroup {}", jobGroupName);

            // Load Job Group Config Properties
            groupActive = configuration.getBooleanConfig(String.format("jobGroupConfig.%s.active", jobGroupName), null,
                    true);

            final String[] groupAppInstanceKeys = configuration.getStringArrayConfig(String.format(
                        "jobGroupConfig.%s.appInstanceKey", jobGroupName), null, true);

            // Create JobGroupConfig from JobGroupName as set in JobConfig and ActiveState from JobConfig
            jobGroupConfig = new JobGroupConfig(jobGroupName, groupActive, Sets.newHashSet(groupAppInstanceKeys));
        } else {
            LOG.trace("Job {} has no configured JobGroup. Relying on JobConfig only.", jobName);
        }

        // Load Job Config Properties
        final String[] appInstanceKeys = configuration.getStringArrayConfig(String.format("jobConfig.%s.appInstanceKey",
                    jobName), null, true);
        final int limit = configuration.getIntegerConfig(String.format("jobConfig.%s.limit", jobName), null, 0);
        final int startupLimit = configuration.getIntegerConfig(String.format("jobConfig.%s.startupLimit", jobName),
                null, limit);
        jobActive = jobsGlobalActive
                && configuration.getBooleanConfig(String.format("jobConfig.%s.active", jobName), null, true);

        // Create Job Config from loaded Props
        final JobConfig retVal = new JobConfig(Sets.newHashSet(appInstanceKeys), limit, startupLimit, jobActive,
                jobGroupConfig);

        // Log the Job Config Properties as loaded from Database
        LOG.debug("Config for {}: {}", job.getBeanName(), retVal);

        return retVal;
    }
}
