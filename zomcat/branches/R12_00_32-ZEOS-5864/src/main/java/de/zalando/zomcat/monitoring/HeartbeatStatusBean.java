package de.zalando.zomcat.monitoring;

import java.io.Serializable;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import org.springframework.stereotype.Component;

import de.zalando.zomcat.HeartbeatMode;

@ManagedResource(objectName = "Zalando:name=Heartbeat Status Bean")
@Component("heartbeatStatusBean")
public class HeartbeatStatusBean implements HeartbeatStatusMBean, Serializable {

    private static final long serialVersionUID = 7398090620088428458L;

    public static final String BEAN_NAME = "heartbeatStatusBean";

    private HeartbeatMode heartbeatMode = HeartbeatMode.OK;

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.HeartbeatStatusMBean#toggleHeartbeatMode()
     */
    @ManagedOperation(description = "Toggles the HeartbeatMode between OK and DEPLOY")
    @Override
    public String toggleHeartbeatMode() {
        if (HeartbeatMode.OK.equals(heartbeatMode)) {
            heartbeatMode = HeartbeatMode.DEPLOY;
        } else {
            heartbeatMode = HeartbeatMode.OK;
        }

        return heartbeatMode.toString();
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.HeartbeatStatusMBean#getHeartbeatMode()
     */
    @ManagedOperation(description = "Returns the actual HeartbeatMode")
    @Override
    public String getHeartbeatMode() {
        if (heartbeatMode == null) {
            return null;
        }

        return heartbeatMode.toString();
    }

    /**
     * @return  the actual HeartbeatMode or <code>null</code> if not set
     */
    public HeartbeatMode getHeartbeatModeAsEnum() {
        if (heartbeatMode == null) {
            return null;
        }

        return heartbeatMode;
    }

    public String getLoadbalancerMessage() {
        if (heartbeatMode == null) {
            return null;
        }

        return heartbeatMode.getLoadbalancerMessage();
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.HeartbeatStatusMBean#setHeartbeatMode(HeartbeatMode)
     */
    @ManagedOperation(description = "sets the new HeartbeatMode")
    @Override
    public void setHeartbeatMode(final HeartbeatMode heartbeatMode) {
        this.heartbeatMode = heartbeatMode;
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.HeartbeatStatusMBean#setHeartbeatMode(java.lang.String)
     */
    @ManagedOperation(description = "sets the new HeartbeatMode")
    @Override
    public void setHeartbeatMode(final String heartbeatModeAsString) {
        this.heartbeatMode = HeartbeatMode.valueOf(heartbeatModeAsString);
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("HeartbeatStatusBean [heartbeatMode=");
        builder.append(heartbeatMode);
        builder.append("]");
        return builder.toString();
    }
}
