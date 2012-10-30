package de.zalando.zomcat.jobs.model;

import de.zalando.zomcat.jobs.FinishedWorkerBean;
import de.zalando.zomcat.jobs.JobTypeStatusBean;

public class JobRow {
    private final boolean isHistoryEnabled;
    private final Class<?> jobClass;

    private String startTime = null;
    private String endTime = null;
    private String duration = null;
    private int workerId;
    private String flowId = null;

    public JobRow(final JobTypeStatusBean jobTypeStatusBean, final boolean isHistoryEnabled) {
        this.jobClass = jobTypeStatusBean.getJobClass();
        this.isHistoryEnabled = isHistoryEnabled;
    }

    public JobRow(final FinishedWorkerBean finishedWorkerBean) {
        startTime = finishedWorkerBean.getStartTimeFormatted();
        endTime = finishedWorkerBean.getEndTimeFormatted();
        duration = finishedWorkerBean.getDuration();
        workerId = finishedWorkerBean.getId();
        flowId = finishedWorkerBean.getFlowId();

        this.jobClass = finishedWorkerBean.getJobClass();
        this.isHistoryEnabled = true;
    }

    public boolean isHistory() {
        return flowId != null;
    }

    public Class<?> getJobClass() {
        return jobClass;
    }

    public boolean isHistoryEnabled() {
        return isHistoryEnabled;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(final String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(final String endTime) {
        this.endTime = endTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(final String duration) {
        this.duration = duration;
    }

    public int getWorkerId() {
        return workerId;
    }

    public void setWorkerId(final int workerId) {
        this.workerId = workerId;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(final String flowId) {
        this.flowId = flowId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((duration == null) ? 0 : duration.hashCode());
        result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
        result = prime * result + ((flowId == null) ? 0 : flowId.hashCode());
        result = prime * result + (isHistoryEnabled ? 1231 : 1237);
        result = prime * result + ((jobClass == null) ? 0 : jobClass.hashCode());
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
        result = prime * result + workerId;
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

        final JobRow other = (JobRow) obj;
        if (duration == null) {
            if (other.duration != null) {
                return false;
            }
        } else if (!duration.equals(other.duration)) {
            return false;
        }

        if (endTime == null) {
            if (other.endTime != null) {
                return false;
            }
        } else if (!endTime.equals(other.endTime)) {
            return false;
        }

        if (flowId == null) {
            if (other.flowId != null) {
                return false;
            }
        } else if (!flowId.equals(other.flowId)) {
            return false;
        }

        if (isHistoryEnabled != other.isHistoryEnabled) {
            return false;
        }

        if (jobClass == null) {
            if (other.jobClass != null) {
                return false;
            }
        } else if (!jobClass.equals(other.jobClass)) {
            return false;
        }

        if (startTime == null) {
            if (other.startTime != null) {
                return false;
            }
        } else if (!startTime.equals(other.startTime)) {
            return false;
        }

        if (workerId != other.workerId) {
            return false;
        }

        return true;
    }

}
