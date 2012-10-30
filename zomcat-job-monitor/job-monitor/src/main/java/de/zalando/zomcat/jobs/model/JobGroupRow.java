package de.zalando.zomcat.jobs.model;

import java.io.Serializable;

public class JobGroupRow implements Serializable {
    private static final long serialVersionUID = 1L;

    private String groupName;
    private boolean visible;

    public JobGroupRow() { }

    public JobGroupRow(final String groupName, final boolean visible) {
        this.groupName = groupName;
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(final String groupName) {
        this.groupName = groupName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
        result = prime * result + (visible ? 1231 : 1237);
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

        final JobGroupRow other = (JobGroupRow) obj;
        if (groupName == null) {
            if (other.groupName != null) {
                return false;
            }
        } else if (!groupName.equals(other.groupName)) {
            return false;
        }

        if (visible != other.visible) {
            return false;
        }

        return true;
    }
}
