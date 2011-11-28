package de.zalando.zomcat.jobs;

import org.joda.time.DateTime;

/**
 * interface for a running worker of a quartz job for getting more detailed information about what the job is doing at
 * each moment.
 *
 * @author  fbrick
 */
public interface RunningWorker {

    /**
     * @return  <b>global unique</b> of running worker
     */
    int getId();

    /**
     * @return  start time of job. This is the creation time of the job triggered by quartz. This is <b>NOT</b> the real
     *          start time of execution (in <code>QuartzJobBean.executeInternal(org.quartz.JobExecutionContext)</code>
     *          ). This time is stored in {@link #getInternalStartTime() getInternalStartTime()}.
     */
    DateTime getStartTime();

    /**
     * @return  the internal start time of job execution. This means the time when <code>
     *          QuartzJobBean.executeInternal(org.quartz.JobExecutionContext)</code> is called. This can be a long time
     *          later than {@link #getStartTime() getStartTime()}.
     */
    DateTime getInternalStartTime();

    /**
     * @return  optional number of actual processed item
     */
    Integer getActualProcessedItemNumber();

    /**
     * @return  optional total number of items to be processed
     */
    Integer getTotalNumberOfItemsToBeProcessed();

    /**
     * @return  optional job description
     */
    String getDescription();

    /**
     * Retrieve the jobHistoryId assigned to this running job.
     *
     * @return  the JobHistoryId assigned to this running job
     */
    String getJobHistoryId();

    /**
     * Set the jobHistoryId assigned to this running job.
     *
     * @param  historyId  the jobHistoryId assigned to this running job
     */
    void setJobHistoryId(String historyId);
}
