package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import de.zalando.zomcat.OperationMode;
import de.zalando.zomcat.jobs.JobMonitorPage;
import de.zalando.zomcat.jobs.JobsStatusBean;

public class OperationModeFragment extends BaseFragment {
    private static final long serialVersionUID = 1L;

    public OperationModeFragment(final MarkupContainer markupProvider, final boolean enabled) {
        super("placeholderForOperationMode", enabled ? "operationModeNormal" : "operationModeMaintenance",
            markupProvider);

        setOutputMarkupPlaceholderTag(true);

        final AjaxLink<JobMonitorPage> operationModeToggleLink = new AjaxLink<JobMonitorPage>("operationModeToggle") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(final AjaxRequestTarget target) {
                final JobsStatusBean jobsStatusBean = getJobMonitorPage().getJobsStatusBean();
                jobsStatusBean.toggleOperationMode();

                final boolean enabled = jobsStatusBean.getOperationModeAsEnum() == OperationMode.NORMAL;

                final OperationModeFragment toggledFragment = new OperationModeFragment(markupProvider, enabled);
                OperationModeFragment.this.replaceWith(toggledFragment);
                target.add(toggledFragment);
                target.add(markupProvider.getPage().get("form:group:listContainer"));
            }
        };

        add(operationModeToggleLink);
    }
}
