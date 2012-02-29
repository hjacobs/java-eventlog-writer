package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import de.zalando.zomcat.HeartbeatMode;
import de.zalando.zomcat.jobs.JobMonitorPage;
import de.zalando.zomcat.monitoring.HeartbeatStatusBean;

public class HeartbeatModeFragment extends BaseFragment {
    private static final long serialVersionUID = 1L;

    public HeartbeatModeFragment(final MarkupContainer markupProvider, final boolean ok) {
        super("placeholderForHeartbeatMode", ok ? "hearbeatModeOk" : "hearbeatModeDeploy", markupProvider);

        setOutputMarkupPlaceholderTag(true);

        final AjaxLink<JobMonitorPage> heartbeatModeToggleLink = new AjaxLink<JobMonitorPage>("hearbeatModeToggle") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(final AjaxRequestTarget target) {
                final HeartbeatStatusBean heartbeatStatusBean = getJobMonitorPage().getHeartbeatStatusBean();
                heartbeatStatusBean.toggleHeartbeatMode();

                final boolean ok = heartbeatStatusBean.getHeartbeatModeAsEnum() == HeartbeatMode.OK;

                final HeartbeatModeFragment toggledFragment = new HeartbeatModeFragment(markupProvider, ok);
                HeartbeatModeFragment.this.replaceWith(toggledFragment);
                target.add(toggledFragment);
            }
        };

        add(heartbeatModeToggleLink);
    }
}
