package de.zalando.zomcat.jobs;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * static view implementation of {@link RunningWorker RunningWorker}.
 *
 * @author  fbrick
 */
public class RunningWorkerBean implements RunningWorker, Serializable {
    private static final long serialVersionUID = -769687955271659257L;

    private final int id;
    private final DateTime startTime;
    private final DateTime internalStartTime;
    private final Integer actualProcessedItemNumber;
    private final Integer totalNumberOfItemsToBeProcessed;
    private final String description;
    private final JobConfig jobConfig;
    private String flowId = null;
    protected Long threadCPUNanoSeconds;

    public RunningWorkerBean(final RunningWorker runningWorker) {
        this(runningWorker.getJobConfig(), runningWorker.getFlowId(), runningWorker.getId(),
            runningWorker.getStartTime(), runningWorker.getActualProcessedItemNumber(),
            runningWorker.getTotalNumberOfItemsToBeProcessed(), runningWorker.getInternalStartTime(),
            runningWorker.getDescription(), runningWorker.getThreadCPUNanoSeconds());
    }

    protected RunningWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime) {
        this(jobConfig, jobHistoryId, id, startTime, null, null);
    }

    protected RunningWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime, final Integer totalNumberOfItemsToBeProcessed) {
        this(jobConfig, jobHistoryId, id, startTime, null, totalNumberOfItemsToBeProcessed);
    }

    protected RunningWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime, final Integer actualProcessedItemNumber,
            final Integer totalNumberOfItemsToBeProcessed) {
        this(jobConfig, jobHistoryId, id, startTime, actualProcessedItemNumber, totalNumberOfItemsToBeProcessed, null);
    }

    protected RunningWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime, final Integer actualProcessedItemNumber,
            final Integer totalNumberOfItemsToBeProcessed, final DateTime internalStartTime) {
        this(jobConfig, jobHistoryId, id, startTime, actualProcessedItemNumber, totalNumberOfItemsToBeProcessed,
            internalStartTime, null, null);
    }

    protected RunningWorkerBean(final JobConfig jobConfig, final String jobHistoryId, final int id,
            final DateTime startTime, final Integer actualProcessedItemNumber,
            final Integer totalNumberOfItemsToBeProcessed, final DateTime internalStartTime, final String description,
            final Long threadCPUNanoSeconds) {
        super();

        this.id = id;
        this.startTime = startTime;
        this.actualProcessedItemNumber = actualProcessedItemNumber;
        this.totalNumberOfItemsToBeProcessed = totalNumberOfItemsToBeProcessed;
        this.internalStartTime = internalStartTime;
        this.description = description;
        this.flowId = jobHistoryId;
        this.jobConfig = jobConfig;
        this.threadCPUNanoSeconds = threadCPUNanoSeconds;
    }

    /**
     * @return  the id
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * @see  RunningWorker#getStartTime()
     */
    @Override
    public DateTime getStartTime() {
        return startTime;
    }

    protected static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss:SSS");

    /**
     * @see  RunningWorker#getStartTime()
     */
    public String getStartTimeFormatted() {
        return DTF.print(startTime);
    }

    /**
     * @see  RunningWorker#getInternalStartTime()
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
     * @see  RunningWorker#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFlowId() {
        if (flowId == null) {
            return String.valueOf(id);
        }

        return flowId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFlowId(final String flowId) {
        this.flowId = flowId;
    }

    @Override
    public Long getThreadCPUNanoSeconds() {
        return threadCPUNanoSeconds;
    }

    @Override
    public JobConfig getJobConfig() {
        return jobConfig;
    }

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
        builder.append(", jobConfig=");
        builder.append(jobConfig);
        builder.append(", jobHistoryId=");
        builder.append(flowId);
        builder.append(", threadCPUNanoSeconds=");
        builder.append(threadCPUNanoSeconds);
        builder.append("]");
        return builder.toString();
    }
}
