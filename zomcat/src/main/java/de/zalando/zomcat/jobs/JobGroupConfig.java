package de.zalando.zomcat.jobs;

import java.io.Serializable;

import java.util.Set;

/**
 * JobGroupConfig is a class that contains simple activation/deactivation stats for groups of jobs.
 *
 * @author  bod
 */
public class JobGroupConfig implements Serializable {
    private static final long serialVersionUID = 1397771322288321306L;

    private final String jobGroupName;

    private final boolean jobGroupActive;
    private final Set<String> groupAppInstanceKeys;

    public JobGroupConfig(final String jobGroupName, final boolean jobGroupActive,
            final Set<String> groupAppInstanceKeys) {
        this.jobGroupName = jobGroupName;
        this.jobGroupActive = jobGroupActive;
        this.groupAppInstanceKeys = groupAppInstanceKeys;
    }

    /**
     * Getter for Field: jobGroupActive
     *
     * @return  the jobGroupActive
     */
    public boolean isJobGroupActive() {
        return jobGroupActive;
    }

    /**
     * Getter for Field: jobGroupName
     *
     * @return  the jobGroupName
     */
    public String getJobGroupName() {
        return jobGroupName;
    }

    public Set<String> getGroupAppInstanceKeys() {
        return groupAppInstanceKeys;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("JobGroupConfig [jobGroupName=");
        builder.append(jobGroupName);
        builder.append(", jobGroupActive=");
        builder.append(jobGroupActive);
        builder.append(", groupAppInstanceKeys=");
        builder.append(groupAppInstanceKeys);
        builder.append("]");
        return builder.toString();
    }
}
