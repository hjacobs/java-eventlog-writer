package de.zalando.zomcat.jobs;

/**
 * JobGroupConfig is a class that contains simple activation/deactivation stats for groups of jobs.
 *
 * @author  bod
 */
public class JobGroupConfig {

    private final String jobGroupName;

    private final boolean jobGroupActive;

    public JobGroupConfig(final String jobGroupName, final boolean jobGroupActive) {
        this.jobGroupName = jobGroupName;
        this.jobGroupActive = jobGroupActive;
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("JobGroupConfig [jobGroupName=");
        builder.append(jobGroupName);
        builder.append(", jobGroupActive=");
        builder.append(jobGroupActive);
        builder.append("]");
        return builder.toString();
    }
}
