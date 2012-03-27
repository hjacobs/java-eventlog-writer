package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import de.zalando.zomcat.jobs.JobMonitorPage;
import de.zalando.zomcat.jobs.JobTypeStatusBean;

public class JobModeFragment extends BaseFragment {
    private static final long serialVersionUID = 1L;

    private final Class<?> jobClass;

    public JobModeFragment(final MarkupContainer markupProvider, final JobTypeStatusBean jobTypeStatusBean,
            final boolean enabled) {
        super("placeholderForJobEnabled",
            enabled
                ? (jobTypeStatusBean.isDisabled() ? "jobDisabled" : "jobEnabled")
                : (jobTypeStatusBean.getJobConfig().isActive()
                    ? (jobTypeStatusBean.isDisabled() ? "jobDisabledRowDisabled" : "jobEnabledRowDisabled")
                    : "jobRowDisabled"), markupProvider);

        jobClass = jobTypeStatusBean.getJobClass();

        setOutputMarkupPlaceholderTag(true);
        if (enabled) {
            final AjaxLink<JobMonitorPage> jobGroupModeToggleLink = new AjaxLink<JobMonitorPage>("jobToggle") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick(final AjaxRequestTarget target) {
                    final JobTypeStatusBean jobTypeStatusBean = getJobMonitorPage().getJobTypeStatusBean(jobClass);
                    jobTypeStatusBean.toggleMode();

                    final JobModeFragment toggledFragment = new JobModeFragment(markupProvider, jobTypeStatusBean,
                            enabled);
                    JobModeFragment.this.replaceWith(toggledFragment);
                    target.add(toggledFragment);
                }
            };

            add(jobGroupModeToggleLink);
        }
    }
}
