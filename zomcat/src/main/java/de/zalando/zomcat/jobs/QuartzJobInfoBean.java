package de.zalando.zomcat.jobs;

/**
 * Information about a quartz job. It is needed to trigger the job again.
 *
 * @author  fbrick
 */
public class QuartzJobInfoBean {

    private String schedulerName = null;
    private String jobName = null;
    private String jobGroup = null;

    public QuartzJobInfoBean(final String schedulerName, final String jobName, final String jobGroup) {
        super();
        this.schedulerName = schedulerName;
        this.jobName = jobName;
        this.jobGroup = jobGroup;
    }

    /**
     * @return  the schedulerName
     */
    public String getSchedulerName() {
        return schedulerName;
    }

    /**
     * @return  the jobName
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * @return  the jobGroup
     */
    public String getJobGroup() {
        return jobGroup;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("QuartzJobInfoBean [schedulerName=");
        builder.append(schedulerName);
        builder.append(", jobName=");
        builder.append(jobName);
        builder.append(", jobGroup=");
        builder.append(jobGroup);
        builder.append("]");
        return builder.toString();
    }
}
