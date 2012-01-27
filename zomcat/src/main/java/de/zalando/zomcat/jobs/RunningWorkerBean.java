package de.zalando.zomcat.jobs;

import org.joda.time.DateTime;

/**
 * static view implementation of {@link RunningWorker RunningWorker}.
 *
 * @author  fbrick
 */
public class RunningWorkerBean implements RunningWorker {

    private final int id;
    private final DateTime startTime;
    private final DateTime internalStartTime;
    private final Integer actualProcessedItemNumber;
    private final Integer totalNumberOfItemsToBeProcessed;
    private final String description;
    private final JobConfig jobConfig;
    private String jobHistoryId = null;

    public RunningWorkerBean(final RunningWorker runningWorker) {
        this(runningWorker.getJobConfig(), runningWorker.getJobHistoryId(), runningWorker.getId(),
            runningWorker.getStartTime(), runningWorker.getActualProcessedItemNumber(),
            runningWorker.getTotalNumberOfItemsToBeProcessed(), runningWorker.getInternalStartTime());
    }

    public RunningWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime) {
        this(jobConfig, jobHistoryId, id, startTime, null, null);
    }

    public RunningWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime, final Integer totalNumberOfItemsToBeProcessed) {
        this(jobConfig, jobHistoryId, id, startTime, null, totalNumberOfItemsToBeProcessed);
    }

    public RunningWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime, final Integer actualProcessedItemNumber,
            final Integer totalNumberOfItemsToBeProcessed) {
        this(jobConfig, jobHistoryId, id, startTime, actualProcessedItemNumber, totalNumberOfItemsToBeProcessed, null);
    }

    public RunningWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime, final Integer actualProcessedItemNumber,
            final Integer totalNumberOfItemsToBeProcessed, final DateTime internalStartTime) {
        this(jobConfig, jobHistoryId, id, startTime, actualProcessedItemNumber, totalNumberOfItemsToBeProcessed,
            internalStartTime, null);
    }

    public RunningWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime, final Integer actualProcessedItemNumber,
            final Integer totalNumberOfItemsToBeProcessed, final DateTime internalStartTime, final String description) {
        super();

        this.id = id;
        this.startTime = startTime;
        this.actualProcessedItemNumber = actualProcessedItemNumber;
        this.totalNumberOfItemsToBeProcessed = totalNumberOfItemsToBeProcessed;
        this.internalStartTime = internalStartTime;
        this.description = description;
        this.jobHistoryId = jobHistoryId;
        this.jobConfig = jobConfig;
    }

    /**
     * @return  the id
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.RunningWorker#getStartTime()
     */
    @Override
    public DateTime getStartTime() {
        return startTime;
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.RunningWorker#getInternalStartTime()
     */
    @Override
    public DateTime getInternalStartTime() {
        return internalStartTime;
    }

    /**
     * @return  the actualProcessedItemNumber
     */
    @Override
    public Integer getActualProcessedItemNumber() {
        return actualProcessedItemNumber;
    }

    /**
     * @return  the totalNumberOfItemsToBeProcessed
     */
    @Override
    public Integer getTotalNumberOfItemsToBeProcessed() {
        return totalNumberOfItemsToBeProcessed;
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.RunningWorker#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJobHistoryId() {
        if (jobHistoryId == null) {
            return String.valueOf(id);
        }

        return jobHistoryId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setJobHistoryId(final String jobHistoryId) {
        this.jobHistoryId = jobHistoryId;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("RunningWorkerBean [id=");
        builder.append(id);
        builder.append(", startTime=");
        builder.append(startTime);
        builder.append(", internalStartTime=");
        builder.append(internalStartTime);
        builder.append(", actualProcessedItemNumber=");
        builder.append(actualProcessedItemNumber);
        builder.append(", totalNumberOfItemsToBeProcessed=");
        builder.append(totalNumberOfItemsToBeProcessed);
        builder.append(", description=");
        builder.append(description);
        builder.append(", jobHistoryId=");
        builder.append(jobHistoryId);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public JobConfig getJobConfig() {
        return jobConfig;
    }
}
