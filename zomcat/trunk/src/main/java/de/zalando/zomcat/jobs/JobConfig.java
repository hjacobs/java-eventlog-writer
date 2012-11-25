package de.zalando.zomcat.jobs;

import java.io.Serializable;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Configuration Object for Jobs. Contains a Set of AppInstanceKeys identifying all AppInstances the respective Job
 * using the JobConfig is allowed be executed on.
 *
 * @author  bod
 */
public class JobConfig implements Serializable {
    private static final long serialVersionUID = 1379125313822695827L;

    /**
     * Asterisk * indicating that all AppInstanceKeys are allowed to execute the respectively current job.
     */
    private static final String ALL_APP_INSTANCE_KEYS_ALLOWED = "*";

    /**
     * Set of AppInstanceKeys allowed to execute the respectivly current job.
     */
    private final Set<String> allowedAppInstanceKeys;

    private final int limit;

    private final int startupLimit;

    private final boolean active;

    private final JobGroupConfig jobGroupConfig;

    /**
     * Default Constructor.
     *
     * @param  allowedAppInstanceKeys  Set of Application Instance Keys allowed to execute the current Job
     * @param  limit                   The Processing Item Amount Limit - at Max process this amount of items in a
     *                                 single job execution
     * @param  startupLimit            The Processing Item Amount Limit for first run of Job after JVM startup,
     *                                 currently not used yet
     * @param  active                  The global active State for any Job of a particular type. If set to <code>
     *                                 false</code> the respective Job is globally deactivated and will not run anywhere
     */
    public JobConfig(final Set<String> allowedAppInstanceKeys, final int limit, final int startupLimit,
            final boolean active, final JobGroupConfig jobGroupConfig) {
        super();
        if (allowedAppInstanceKeys == null) {
            throw new IllegalArgumentException("The Set of allowed AppInstanceKeys cannot be NULL."
                    + " Please set a correct Set of allowed AppInstanceKeys.");
        }

        this.allowedAppInstanceKeys = ImmutableSet.copyOf(allowedAppInstanceKeys);
        this.limit = limit;
        this.startupLimit = startupLimit;
        this.active = active;
        this.jobGroupConfig = jobGroupConfig;
    }

    /**
     * Set of appInstanceKeys on, which machines the job is allowed to run.
     *
     * @return
     */
    public Set<String> getAllowedAppInstanceKeys() {
        return allowedAppInstanceKeys;
    }

    /**
     * Check if given appInstanceKey is contained in Set of allowed AppInstanceKeys. This method is used for
     * preexecution checks for any job dependent on {@link JobConfig} instance for its configuration
     *
     * @param   appInstanceKey  The AppInstanceKey to check
     *
     * @return  <code>true</code> if given AppInstanceKey is contained in {@link JobConfig}s Set of allowed
     *          AppInstanceKeys, OR if the {@link JobConfig}s Set of allowed AppInstanceKeys contains the '*' asterisk,
     *          OR if the {@link JobConfig}s Set of AppInstanceKeys isEmpty AND the {@link JobGroupConfig}s Set of
     *          AppInstanceKeys contains either the current AppInstanceKey or the '*' asterisk character. Otherwise
     *          <code>false</code> is returned
     */
    public boolean isAllowedAppInstanceKey(final String appInstanceKey) {

        // If JobConfig.appInstanceKeys.contains(curAppInstanceKey) OR
        // JobConfig.appInstanceKeys.contains(ALL_APP_INSTANCE_KEYS) OR
        // (JobAppInstanceKeysSet.isEmpty && JobGroupConfig != null AND
        // (JobGroupConfig.appInstanceKeys.contains(curAppInstanceKey) ||
        // JobGroupConfig.appInstanceKey.contains(ALL_APP_INSTANCE_KEYS)))
        return allowedAppInstanceKeys.contains(appInstanceKey)
                || allowedAppInstanceKeys.contains(ALL_APP_INSTANCE_KEYS_ALLOWED)
                || (allowedAppInstanceKeys.isEmpty()
                    && (jobGroupConfig != null && jobGroupConfig.isAllowedAppInstanceKey(appInstanceKey)));
    }

    /**
     * Limit for the fetcher jobs.
     *
     * @return
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Limit for the fetcher jobs, after deployment. These limit should be lower than the other one.
     *
     * @return
     */
    public int getStartupLimit() {
        return startupLimit;
    }

    /**
     * Check whether or not the current Job is active.
     *
     * @return
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Getter for JobGroupConfig associated with Instance of this class.
     *
     * @return  The {@link JobGroupConfig} instance if there is any otherwise return null
     */
    public JobGroupConfig getJobGroupConfig() {
        return jobGroupConfig;
    }

    /**
     * Getter for JobGroupConfig associated with Instance of this class.
     *
     * @return  The {@link JobGroupConfig} instance if there is any otherwise return null
     */
    public String getJobGroupName() {
        if (jobGroupConfig != null) {
            return jobGroupConfig.getJobGroupName();
        }

        return JobGroupConfig.DEFAULT_GROUP_NAME;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 37;
        int result = 1;
        result = prime * result + (active ? 1231 : 1237);
        result = prime * result + ((allowedAppInstanceKeys == null) ? 0 : allowedAppInstanceKeys.hashCode());
        result = prime * result + ((jobGroupConfig == null) ? 0 : jobGroupConfig.hashCode());
        result = prime * result + limit;
        result = prime * result + startupLimit;
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final JobConfig other = (JobConfig) obj;
        if (active != other.active) {
            return false;
        }

        if (allowedAppInstanceKeys == null) {
            if (other.allowedAppInstanceKeys != null) {
                return false;
            }
        } else if (!allowedAppInstanceKeys.equals(other.allowedAppInstanceKeys)) {
            return false;
        }

        if (jobGroupConfig == null) {
            if (other.jobGroupConfig != null) {
                return false;
            }
        } else if (!jobGroupConfig.equals(other.jobGroupConfig)) {
            return false;
        }

        if (limit != other.limit) {
            return false;
        }

        if (startupLimit != other.startupLimit) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("JobConfig [allowedAppInstanceKeys=");
        builder.append(allowedAppInstanceKeys);
        builder.append(", limit=");
        builder.append(limit);
        builder.append(", startupLimit=");
        builder.append(startupLimit);
        builder.append(", active=");
        builder.append(active);
        builder.append(", jobGroupConfig=");
        builder.append(jobGroupConfig);
        builder.append("]");
        return builder.toString();
    }

}
