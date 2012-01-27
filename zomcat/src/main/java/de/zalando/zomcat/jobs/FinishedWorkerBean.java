package de.zalando.zomcat.jobs;

import org.joda.time.DateTime;

/**
 * bean holding information about a finished worker for history purposes. Endtime of this bean is automatically set when
 * bean is created.
 *
 * @author  fbrick
 */
public class FinishedWorkerBean extends RunningWorkerBean {

    private final DateTime endTime = new DateTime();

    public FinishedWorkerBean(final RunningWorker runningWorker) {
        this(runningWorker.getJobConfig(), runningWorker.getJobHistoryId(), runningWorker.getId(),
            runningWorker.getStartTime(), runningWorker.getActualProcessedItemNumber(),
            runningWorker.getTotalNumberOfItemsToBeProcessed(), runningWorker.getInternalStartTime());
    }

    public FinishedWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime) {
        this(jobConfig, jobHistoryId, id, startTime, null, null);
    }

    public FinishedWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime, final Integer totalNumberOfItemsToBeProcessed) {
        this(jobConfig, jobHistoryId, id, startTime, null, totalNumberOfItemsToBeProcessed);
    }

    public FinishedWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime, final Integer actualProcessedItemNumber,
            final Integer totalNumberOfItemsToBeProcessed) {
        this(jobConfig, jobHistoryId, id, startTime, actualProcessedItemNumber, totalNumberOfItemsToBeProcessed, null);
    }

    public FinishedWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime, final Integer actualProcessedItemNumber,
            final Integer totalNumberOfItemsToBeProcessed, final DateTime internalStartTime) {
        super(jobConfig, jobHistoryId, id, startTime, actualProcessedItemNumber, totalNumberOfItemsToBeProcessed,
            internalStartTime);
    }

    /**
     * @return  the endTime
     */
    public DateTime getEndTime() {
        return endTime;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("FinishedWorkerBean [endTime=");
        builder.append(endTime);
        builder.append(", toString()=");
        builder.append(super.toString());
        builder.append("]");
        return builder.toString();
    }
}
