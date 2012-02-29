package de.zalando.zomcat.jobs;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * a single type of job group with status.
 *
 * @author  carsten.wolters
 */
public class JobGroupTypeStatusBean {
    private final JobGroupConfig jobGroupConfig;

    private final DateTime lastModified = null;
    private boolean disabled = false;

    public JobGroupTypeStatusBean(final JobGroupConfig jobGroupConfig) {
        this.jobGroupConfig = jobGroupConfig;
    }

    /**
     * @return  the lastModified
     */
    public DateTime getLastModified() {
        return lastModified;
    }

    private static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss:SSS");

    /**
     * @return  the last modified as a formatted String or <code>null</code> if not changed at all so far
     */
    public String getLastModifiedFormatted() {
        if (lastModified == null) {
            return null;
        }

        return DTF.print(lastModified);
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    public JobGroupConfig getJobGroupConfig() {
        return jobGroupConfig;
    }

    public void toggleMode() {
        this.disabled = !disabled;
    }

    public String getJobGroupName() {
        if (jobGroupConfig == null) {
            return JobGroupConfig.DEFAULT_GROUP_NAME;
        }

        return jobGroupConfig.getJobGroupName();
    }
}
