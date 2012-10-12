package de.zalando.zomcat.jobs;

import org.joda.time.DateTime;
import org.joda.time.Duration;

/**
 * bean holding information about a finished worker for history purposes. Endtime of this bean is automatically set when
 * bean is created.
 *
 * @author  fbrick
 */
public class FinishedWorkerBean extends RunningWorkerBean {
    private static final long serialVersionUID = -840533955559658403L;

    private final DateTime endTime = new DateTime();

    private Class<?> jobClass;

    public FinishedWorkerBean(final RunningWorker runningWorker) {
        super(runningWorker.getJobConfig(), runningWorker.getFlowId(), runningWorker.getId(),
            runningWorker.getStartTime(), runningWorker.getActualProcessedItemNumber(),
            runningWorker.getTotalNumberOfItemsToBeProcessed(), runningWorker.getInternalStartTime(),
            runningWorker.getDescription(), runningWorker.getThreadCPUNanoSeconds());
        this.jobClass = runningWorker.getClass();
    }

    public FinishedWorkerBean(final RunningWorker runningWorker, final JobConfig jobConfig) {
        super(jobConfig, runningWorker.getFlowId(), runningWorker.getId(), runningWorker.getStartTime(),
            runningWorker.getActualProcessedItemNumber(), runningWorker.getTotalNumberOfItemsToBeProcessed(),
            runningWorker.getInternalStartTime(), runningWorker.getDescription(),
            runningWorker.getThreadCPUNanoSeconds());
        this.jobClass = runningWorker.getClass();
    }

    protected FinishedWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime) {
        this(jobConfig, jobHistoryId, id, startTime, null, null);
    }

    protected FinishedWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime, final Integer totalNumberOfItemsToBeProcessed) {
        this(jobConfig, jobHistoryId, id, startTime, null, totalNumberOfItemsToBeProcessed);
    }

    protected FinishedWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime, final Integer actualProcessedItemNumber,
            final Integer totalNumberOfItemsToBeProcessed) {
        this(jobConfig, jobHistoryId, id, startTime, actualProcessedItemNumber, totalNumberOfItemsToBeProcessed, null,
            null, null);
    }

    protected FinishedWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime, final Integer actualProcessedItemNumber,
            final Integer totalNumberOfItemsToBeProcessed, final DateTime internalStartTime, final String description,
            final Long threadCPUNanoSeconds) {
        super(jobConfig, jobHistoryId, id, startTime, actualProcessedItemNumber, totalNumberOfItemsToBeProcessed,
            internalStartTime, description, threadCPUNanoSeconds);
    }

    /**
     * @return  the endTime
     */
    public DateTime getEndTime() {
        return endTime;
    }

    public String getEndTimeFormatted() {
        return DTF.print(endTime);
    }

    public String getDuration() {
        return String.valueOf(new Duration(getStartTime(), endTime).getMillis());
    }

    public Class<?> getJobClass() {
        return jobClass;
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
