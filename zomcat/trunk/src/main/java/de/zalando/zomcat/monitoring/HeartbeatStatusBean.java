package de.zalando.zomcat.monitoring;

import java.io.Serializable;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import org.springframework.stereotype.Component;

import de.zalando.zomcat.HeartbeatMode;
import de.zalando.zomcat.HostStatus;
import de.zalando.zomcat.util.FileBackedToggle;

@ManagedResource(objectName = "Zalando:name=Heartbeat Status Bean")
@Component("heartbeatStatusBean")
public class HeartbeatStatusBean implements HeartbeatStatusMBean, Serializable {

    private static final long serialVersionUID = 7398090620088428458L;

    public static final String BEAN_NAME = "heartbeatStatusBean";

    private final FileBackedToggle heartbeatEnabled = new FileBackedToggle("zomcat-heartbeat-disabled", true);

    /**
     * @see  HeartbeatStatusMBean#toggleHeartbeatMode()
     */
    @ManagedOperation(description = "Toggles the HeartbeatMode between OK and DEPLOY")
    @Override
    public String toggleHeartbeatMode() {
        heartbeatEnabled.toggle();
        return getHeartbeatMode();
    }

    /**
     * @see  HeartbeatStatusMBean#getHeartbeatMode()
     */
    @ManagedOperation(description = "Returns the actual HeartbeatMode")
    @Override
    public String getHeartbeatMode() {
        return getHeartbeatModeAsEnum().toString();
    }

    /**
     * @return  the actual HeartbeatMode or <code>null</code> if not set
     */
    public HeartbeatMode getHeartbeatModeAsEnum() {
        return heartbeatEnabled.get() ? HeartbeatMode.OK : HeartbeatMode.DEPLOY;
    }

    public String getLoadbalancerMessage() {
        if (!HostStatus.isAllocated()) {

            // make sure that the LB status is disabled if the host-status is not production ready
            return HeartbeatMode.DEPLOY.getLoadbalancerMessage();
        }

        return getHeartbeatModeAsEnum().getLoadbalancerMessage();
    }

    /**
     * @see  HeartbeatStatusMBean#setHeartbeatMode(HeartbeatMode)
     */
    @ManagedOperation(description = "sets the new HeartbeatMode")
    @Override
    public void setHeartbeatMode(final HeartbeatMode heartbeatMode) {
        heartbeatEnabled.set(heartbeatMode == HeartbeatMode.OK);
    }

    /**
     * @see  HeartbeatStatusMBean#setHeartbeatMode(java.lang.String)
     */
    @ManagedOperation(description = "sets the new HeartbeatMode")
    @Override
    public void setHeartbeatMode(final String heartbeatModeAsString) {
        setHeartbeatMode(HeartbeatMode.valueOf(heartbeatModeAsString));
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("HeartbeatStatusBean");
        return builder.toString();
    }
}
