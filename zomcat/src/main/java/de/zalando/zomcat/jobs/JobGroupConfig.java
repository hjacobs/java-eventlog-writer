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

    public static final String DEFAULT_GROUP_NAME = "none";

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
        return jobGroupName == null ? DEFAULT_GROUP_NAME : jobGroupName;
    }

    public Set<String> getGroupAppInstanceKeys() {
        return groupAppInstanceKeys;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jobGroupName == null) ? 0 : jobGroupName.hashCode());
        return result;
    }

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

        final JobGroupConfig other = (JobGroupConfig) obj;
        if (jobGroupName == null) {
            if (other.jobGroupName != null) {
                return false;
            }
        } else if (!jobGroupName.equals(other.jobGroupName)) {
            return false;
        }

        return true;
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
