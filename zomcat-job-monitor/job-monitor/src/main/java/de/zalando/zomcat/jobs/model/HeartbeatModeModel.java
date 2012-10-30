package de.zalando.zomcat.jobs.model;

import org.apache.wicket.spring.injection.annot.SpringBean;

import de.zalando.zomcat.HeartbeatMode;
import de.zalando.zomcat.monitoring.HeartbeatStatusBean;

public class HeartbeatModeModel extends BaseLoadableDetachableModel<HeartbeatMode> {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private HeartbeatStatusBean heartbeatStatusBean;

    @Override
    protected HeartbeatMode load() {
        return heartbeatStatusBean.getHeartbeatModeAsEnum();
    }

    public void toggle() {
        heartbeatStatusBean.toggleHeartbeatMode();
    }
}
