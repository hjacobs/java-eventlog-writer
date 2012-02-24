package de.zalando.zomcat.jobs.model;

import java.io.Serializable;

import de.zalando.zomcat.jobs.FinishedWorkerBean;
import de.zalando.zomcat.jobs.JobTypeStatusBean;

public class JobRow implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean isHistoryEnabled;
    private final Class<?> jobClass;
    private final transient FinishedWorkerBean finishedWorkerBean;

    public JobRow(final JobTypeStatusBean jobTypeStatusBean, final boolean isHistoryEnabled) {
        this.jobClass = jobTypeStatusBean.getJobClass();
        finishedWorkerBean = null;
        this.isHistoryEnabled = isHistoryEnabled;
    }

    public JobRow(final FinishedWorkerBean finishedWorkerBean) {
        this.finishedWorkerBean = finishedWorkerBean;
        this.jobClass = null;
        this.isHistoryEnabled = true;
    }

    public boolean isHistory() {
        return finishedWorkerBean != null;
    }

    public Class<?> getJobClass() {
        return jobClass;
    }

    public FinishedWorkerBean getFinishedWorkerBean() {
        return finishedWorkerBean;
    }

    public boolean isHistoryEnabled() {
        return isHistoryEnabled;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((finishedWorkerBean == null) ? 0 : finishedWorkerBean.hashCode());
        result = prime * result + ((jobClass == null) ? 0 : jobClass.hashCode());
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
        if (finishedWorkerBean == null) {
            if (other.finishedWorkerBean != null) {
                return false;
            }
        } else if (!finishedWorkerBean.equals(other.finishedWorkerBean)) {
            return false;
        }

        if (jobClass == null) {
            if (other.jobClass != null) {
                return false;
            }
        } else if (!jobClass.equals(other.jobClass)) {
            return false;
        }

        return true;
    }

}
