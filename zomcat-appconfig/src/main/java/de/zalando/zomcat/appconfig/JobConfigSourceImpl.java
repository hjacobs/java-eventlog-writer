package de.zalando.zomcat.appconfig;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import de.zalando.appconfig.ConfigCtx;
import de.zalando.appconfig.Configuration;

import de.zalando.domain.ComponentBean;

import de.zalando.zomcat.configuration.AppInstanceKeySource;
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

    private static final Logger LOG = Logger.getLogger(JobConfigSourceImpl.class);

    public abstract Configuration getConfig();

    @Override
    public final JobConfig getJobConfig(final ComponentBean job) {

        // By Default the JobGroup the Job belongs to (even if the Job has no JobGroup) is active
        boolean groupActive = true;

        // By Default the Job is deactivated itself (safety precaution)
        boolean jobActive = false;

        // Get Job Bean Name which is used to create JobConfig Property Names
        final String jobName = job.getBeanName();

        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Attempting to create JobConfig for Job: %s", jobName));
        }

        // Load the JobGroup for given Job
        String jobGroupName = getConfig().getStringConfig(String.format("jobConfig.%s.jobGroup", jobName),
                new ConfigCtx(null, null), null);

        // If there is a Job Group defined on Job Config, fetch the JobGroup Active Status
        JobGroupConfig jobGroupConfig = null;
        if (jobGroupName != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Attempting to fetch create JobGroupConfig for JobGroup: %s", jobGroupName));
            }

            // Load Job Group Config Properties
            groupActive = getConfig().getBooleanConfig(String.format("jobGroupConfig.%s.active", jobGroupName),
                    new ConfigCtx(null, null));

            // Create JobGroupConfig from JobGroupName as set in JobConfig and ActiveState from JobConfig
            jobGroupConfig = new JobGroupConfig(jobGroupName, groupActive);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Job: %s has no configured JobGroup. Relying on JobConfig only.", jobName));
            }
        }

        // Load Job Config Properties
        final String[] appInstanceKeys = getConfig().getStringArrayConfig(String.format("jobConfig.%s.appInstanceKey",
                    jobName), new ConfigCtx(null, null), true);
        final int limit = getConfig().getIntegerConfig(String.format("jobConfig.%s.limit", jobName),
                new ConfigCtx(null, null), 0);
        final int startupLimit = getConfig().getIntegerConfig(String.format("jobConfig.%s.startupLimit", jobName),
                new ConfigCtx(null, null), limit);
        jobActive = getConfig().getBooleanConfig(String.format("jobConfig.%s.active", jobName),
                new ConfigCtx(null, null), true);

        // Create Job Config from loaded Props
        JobConfig retVal = new JobConfig(Sets.newHashSet(appInstanceKeys), limit, startupLimit, jobActive,
                jobGroupConfig);

        // Log the Job Config Properties as loaded from Database
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("Created JobConfig for Job: %s.  JobConfig is: %s", job.getBeanName(),
                    retVal.toString()));
        }

        return retVal;
    }
}
