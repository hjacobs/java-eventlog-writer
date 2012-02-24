package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import de.zalando.zomcat.jobs.JobMonitorPage;
import de.zalando.zomcat.jobs.JobsStatusBean;
import de.zalando.zomcat.jobs.model.JobGroupRow;

public class JobGroupModeFragment extends BaseFragment {
    private static final long serialVersionUID = 1L;

    public JobGroupModeFragment(final MarkupContainer markupProvider, final JobGroupRow jobGroupRow,
            final JobsStatusBean jobsStatusBean) {
        super("placeholderForJobGroupEnabled",
            jobsStatusBean.isJobGroupDisabled(jobGroupRow.getGroupName()) ? "jobGroupDisabled" : "jobGroupEnabled",
            markupProvider);

        setOutputMarkupPlaceholderTag(true);

        final AjaxLink<JobMonitorPage> jobGroupModeToggleLink = new AjaxLink<JobMonitorPage>("jobGroupToggle") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(final AjaxRequestTarget target) {
                final JobsStatusBean jobsStatusBean = getJobMonitorPage().getJobsStatusBean();
                jobsStatusBean.toggleJobGroup(jobGroupRow.getGroupName());

                final JobGroupModeFragment toggledFragment = new JobGroupModeFragment(markupProvider, jobGroupRow,
                        jobsStatusBean);
                JobGroupModeFragment.this.replaceWith(toggledFragment);
                target.add(toggledFragment);
            }
        };

        add(jobGroupModeToggleLink);
    }
}
