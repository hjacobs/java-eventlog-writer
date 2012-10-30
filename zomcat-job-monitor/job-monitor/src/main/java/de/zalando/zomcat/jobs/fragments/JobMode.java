package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.zalando.zomcat.OperationMode;
import de.zalando.zomcat.jobs.JobMonitorPage;
import de.zalando.zomcat.jobs.JobTypeStatusBean;
import de.zalando.zomcat.jobs.JobsStatusBean;
import de.zalando.zomcat.jobs.model.JobRow;
import de.zalando.zomcat.jobs.model.JobRowsModel;

public class JobMode extends WebMarkupContainer {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private JobsStatusBean jobsStatusBean;

    private final Class<?> jobClass;

    public JobMode(final String id, final IModel<JobRow> jobRow, final JobRowsModel jobRowsModel) {
        super(id, jobRowsModel);

        jobClass = jobRow.getObject().getJobClass();

        final JobTypeStatusBean jobTypeStatusBean = jobsStatusBean.getJobTypeStatusBean(jobClass);
        final boolean enabled = jobTypeStatusBean.getJobConfig().isActive()
                && jobsStatusBean.getOperationModeAsEnum() == OperationMode.NORMAL;

        if (enabled) {
            final AjaxLink<JobMonitorPage> jobGroupModeToggleLink = new AjaxLink<JobMonitorPage>("jobToggle") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick(final AjaxRequestTarget target) {
                    final JobTypeStatusBean jobTypeStatusBean = jobsStatusBean.getJobTypeStatusBean(jobClass);
                    jobTypeStatusBean.toggleMode();
                    target.add(getParent().getParent());
                }
            };

            add(jobGroupModeToggleLink);
        }
    }

    @Override
    protected void onConfigure() {

        final JobTypeStatusBean jobTypeStatusBean = jobsStatusBean.getJobTypeStatusBean(jobClass);
        final boolean enabled = jobTypeStatusBean.getJobConfig().isActive()
                && jobsStatusBean.getOperationModeAsEnum() == OperationMode.NORMAL;

        final Boolean showThisElement = enabled
            ? (jobTypeStatusBean.isDisabled() ? "jobDisabled".equals(getId()) : "jobEnabled".equals(getId()))
            : (jobTypeStatusBean.getJobConfig().isActive()
                ? (jobTypeStatusBean.isDisabled() ? "jobDisabledRowDisabled".equals(getId())
                                                  : "jobEnabledRowDisabled".equals(getId()))
                : "jobRowDisabled".equals(getId()));

        setVisibilityAllowed(showThisElement);
    }

}
