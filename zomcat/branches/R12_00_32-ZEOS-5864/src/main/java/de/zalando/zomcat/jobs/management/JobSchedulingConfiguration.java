package de.zalando.zomcat.jobs.management;

import java.util.Map;

import de.zalando.zomcat.jobs.JobConfig;

/**
 * Simple Job Scheduling Config used to schedule Jobs. Used in conjunction with
 * {@link JobSchedulingConfigurationProvider} implementations to provide the {@link SchedulerFactory} with Jobs to be
 * scheduled
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public class JobSchedulingConfiguration {

    private final JobSchedulingConfigurationType jobType;

    private Long startDelayMS;

    private Long intervalMS;

    private String cronExpression;

    private final String jobClass;

    private final Map<String, String> jobData;

    private JobConfig jobConfig;

    /**
     * Constructor for CRON based JobConfig.
     *
     * @param  cronExpression  The CronExpression to use for Job Scheduling
     * @param  jobClass        The Jobs FQ Classname
     * @param  jobData         Job Data as {@link String} <-> {@link String} {@link Map}
     */
    public JobSchedulingConfiguration(final String cronExpression, final String jobClass,
            final Map<String, String> jobData) {
        this(cronExpression, jobClass, jobData, null);
    }

    /**
     * Constructor for CRON based JobConfig.
     *
     * @param  cronExpression  The CronExpression to use for Job Scheduling
     * @param  jobClass        The Jobs FQ Classname
     * @param  jobData         Job Data as {@link String} <-> {@link String} {@link Map}
     * @param  jobConfig       The Job Activation Configuration
     */
    public JobSchedulingConfiguration(final String cronExpression, final String jobClass,
            final Map<String, String> jobData, final JobConfig jobConfig) {
        jobType = JobSchedulingConfigurationType.CRON;
        this.cronExpression = cronExpression;
        this.jobClass = jobClass;
        this.jobData = jobData;
        this.jobConfig = jobConfig;
    }

    /**
     * Constructor for Simple/Interval based JobConfig.
     *
     * @param  startDelayMS  The Delay after Application Initialization in Milliseconds at which the Job should run for
     *                       the first time
     * @param  intervalMS    The Repetition Interval in Milliseconds for Job Repititions
     * @param  jobClass      The JobClass to use
     * @param  jobData       Job Data as {@link String} <-> {@link String} {@link Map}
     */
    public JobSchedulingConfiguration(final long startDelayMS, final long intervalMS, final String jobClass,
            final Map<String, String> jobData) {
        this(startDelayMS, intervalMS, jobClass, jobData, null);
    }

    /**
     * Constructor for Simple/Interval based JobConfig.
     *
     * @param  startDelayMS  The Delay after Application Initialization in Milliseconds at which the Job should run for
     *                       the first time
     * @param  intervalMS    The Repetition Interval in Milliseconds for Job Repititions
     * @param  jobClass      The JobClass to use
     * @param  jobData       Job Data as {@link String} <-> {@link String} {@link Map}
     */
    public JobSchedulingConfiguration(final long startDelayMS, final long intervalMS, final String jobClass,
            final Map<String, String> jobData, final JobConfig jobConfig) {
        jobType = JobSchedulingConfigurationType.SIMPLE;
        this.startDelayMS = startDelayMS;
        this.intervalMS = intervalMS;
        this.jobClass = jobClass;
        this.jobData = jobData;
        this.jobConfig = jobConfig;
    }

    /**
     * Getter for Field: jobType
     *
     * @return  the jobType
     */
    public JobSchedulingConfigurationType getJobType() {
        return jobType;
    }

    /**
     * Getter for Field: startDelayMS
     *
     * @return  the startDelayMS
     */
    public Long getStartDelayMS() {
        return startDelayMS;
    }

    /**
     * Getter for Field: intervalMS
     *
     * @return  the intervalMS
     */
    public Long getIntervalMS() {
        return intervalMS;
    }

    /**
     * Getter for Field: cronExpression
     *
     * @return  the cronExpression
     */
    public String getCronExpression() {
        return cronExpression;
    }

    /**
     * Getter for Field: jobClass
     *
     * @return  the jobClass
     */
    public String getJobClass() {
        return jobClass;
    }

    public Class<?> getJobJavaClass() throws ClassNotFoundException {
        return Class.forName(getJobClass());
    }

    public String getJobName() throws ClassNotFoundException {
        final String s = getJobJavaClass().getSimpleName();
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    /**
     * Getter for Field: jobData
     *
     * @return  the jobData
     */
    public Map<String, String> getJobData() {
        return jobData;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((jobClass == null) ? 0 : jobClass.hashCode());
        result = prime * result + ((jobData == null) ? 0 : jobData.hashCode());
        result = prime * result + ((jobType == null) ? 0 : jobType.hashCode());
        return result;
    }

    /**
     * Getter for {@link JobConfig}.
     *
     * @return  The JobConfig
     */
    public JobConfig getJobConfig() {
        return jobConfig;
    }

    /**
     * Setter for Field: jobConfig
     *
     * @param  jobConfig  the jobConfig to set
     */
    public void setJobConfig(final JobConfig jobConfig) {
        this.jobConfig = jobConfig;
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

        final JobSchedulingConfiguration other = (JobSchedulingConfiguration) obj;
        if (jobClass == null) {
            if (other.jobClass != null) {
                return false;
            }
        } else if (!jobClass.equals(other.jobClass)) {
            return false;
        }

        if (jobData == null) {
            if (other.jobData != null) {
                return false;
            }
        } else if (!jobData.equals(other.jobData)) {
            return false;
        }

        if (jobType != other.jobType) {
            return false;
        }

        return true;
    }

    public boolean isEqual(final JobSchedulingConfiguration compare) {
        if (compare == null) {
            return false;
        }

        // Test if Job is equally configured
        boolean isSchedulingEqual = false;
        if (JobSchedulingConfigurationType.CRON == this.getJobType()) {
            isSchedulingEqual = this.getCronExpression() != null
                    && this.getCronExpression().equals(compare.getCronExpression());
        } else {
            isSchedulingEqual = (this.getIntervalMS() != null && this.getIntervalMS().equals(compare.getIntervalMS()))
                    & (this.getStartDelayMS() != null && this.getStartDelayMS().equals(compare.getStartDelayMS()));
        }

        boolean isJobConfigEqual = true;
        if (this.jobConfig != null && compare.jobConfig != null) {
            isJobConfigEqual = this.getJobConfig().hashCode() == compare.getJobConfig().hashCode();
        }

        // If base data equals (equals), Scheduling equals and JobConfig
        if (this.equals(compare) && isSchedulingEqual && isJobConfigEqual) {
            return true;
        }

        return false;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("JobSchedulingConfig [jobType=");
        builder.append(jobType);
        if (jobType == JobSchedulingConfigurationType.SIMPLE) {
            builder.append(", startDelayMS=");
            builder.append(startDelayMS);
            builder.append(", intervalMS=");
            builder.append(intervalMS);
        } else {
            builder.append(", cronExpression=");
            builder.append(cronExpression);
        }

        builder.append(", jobClass=");
        builder.append(jobClass);
        builder.append(", jobData=");
        builder.append(jobData);
        builder.append("]");
        return builder.toString();
    }

}
