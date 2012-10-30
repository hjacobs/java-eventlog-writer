package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.markup.html.WebMarkupContainer;

import de.zalando.zomcat.jobs.model.HeartbeatModeModel;

public class HeartbeatModeFragment extends WebMarkupContainer {
    private static final long serialVersionUID = 1L;

    public HeartbeatModeFragment(final String id, final HeartbeatModeModel heartbeatModeModel) {
        super(id, heartbeatModeModel);

        setOutputMarkupPlaceholderTag(true);

        add(new HeartbeatMode("hearbeatModeOk", heartbeatModeModel, de.zalando.zomcat.HeartbeatMode.OK));
        add(new HeartbeatMode("hearbeatModeDeploy", heartbeatModeModel, de.zalando.zomcat.HeartbeatMode.DEPLOY));
    }
}
