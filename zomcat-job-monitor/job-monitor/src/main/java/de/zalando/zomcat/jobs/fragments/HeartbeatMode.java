package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;

import de.zalando.zomcat.jobs.JobMonitorPage;
import de.zalando.zomcat.jobs.model.HeartbeatModeModel;

public class HeartbeatMode extends WebMarkupContainer {
    private static final long serialVersionUID = 1L;

    de.zalando.zomcat.HeartbeatMode heartbeatMode;

    public HeartbeatMode(final String id, final HeartbeatModeModel heartbeatModeModel,
            final de.zalando.zomcat.HeartbeatMode heartbeatMode) {
        super(id, heartbeatModeModel);

        this.heartbeatMode = heartbeatMode;
        setOutputMarkupPlaceholderTag(true);

        final AjaxLink<JobMonitorPage> heartbeatModeToggleLink = new AjaxLink<JobMonitorPage>("hearbeatModeToggle") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(final AjaxRequestTarget target) {
                heartbeatModeModel.toggle();
                target.add(getParent().getParent());
            }
        };

        add(heartbeatModeToggleLink);
    }

    @Override
    protected void onConfigure() {
        setVisibilityAllowed(getDefaultModelObject().equals(heartbeatMode));
    }
}
