package de.zalando.maintenance.domain;

import java.util.Date;

import javax.persistence.Column;

import de.zalando.zomcat.jobs.batch.transition.JobItem;

public class MaintenanceTask implements JobItem {

    @Column
    private long id;

    @Column
    private String type;

    @Column
    private String parameter;

    @Column
    private Date dueDate;

    @Column
    private MaintenanceTaskStatus status;

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(final String parameter) {
        this.parameter = parameter;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(final Date dueDate) {
        this.dueDate = dueDate;
    }

    public MaintenanceTaskStatus getStatus() {
        return status;
    }

    public void setStatus(final MaintenanceTaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MaintenanceTask{" + "id=" + id + ", type='" + type + '\'' + ", parameter='" + parameter + '\''
                + ", dueDate=" + dueDate + ", status=" + status + '}';
    }

}
