package de.zalando.zomcat.monitoring;

import de.zalando.zomcat.HeartbeatMode;

public interface HeartbeatStatusMBean {

    /**
     * toggles heartbeatMode.
     *
     * @return  the new {@link HeartbeatMode HeartbeatMode}
     */
    String toggleHeartbeatMode();

    /**
     * @return  the heartbeatMode
     */
    String getHeartbeatMode();

    /**
     * @param  heartbeatMode  the {@link HeartbeatMode HeartbeatMode} to set
     */
    void setHeartbeatMode(HeartbeatMode heartbeatMode);

    /**
     * @param  heartbeatMode  the {@link HeartbeatMode HeartbeatMode} to set
     */
    void setHeartbeatMode(String heartbeatMode);
}
