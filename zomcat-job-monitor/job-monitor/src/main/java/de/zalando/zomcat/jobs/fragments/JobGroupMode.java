package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.zalando.zomcat.jobs.JobMonitorPage;
import de.zalando.zomcat.jobs.JobsStatusBean;
import de.zalando.zomcat.jobs.model.JobGroupRow;
import de.zalando.zomcat.jobs.model.JobGroupRowModel;

public class JobGroupMode extends WebMarkupContainer {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private JobsStatusBean jobsStatusBean;

    private String jobGroupRowName;

    public JobGroupMode(final String id, final JobGroupRowModel jobGroupRowModel, final JobGroupRow jobGroupRow) {
        super(id, jobGroupRowModel);

        this.jobGroupRowName = jobGroupRow.getGroupName();

        final AjaxLink<JobMonitorPage> jobGroupModeToggleLink = new AjaxLink<JobMonitorPage>("jobGroupToggle") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(final AjaxRequestTarget target) {
                jobsStatusBean.toggleJobGroup(jobGroupRowName);
                target.add(getPage().get("form:group:listContainer"));
            }
        };

        add(jobGroupModeToggleLink);
    }

    @Override
    protected void onConfigure() {
        if ("jobGroupEnabled".equals(getId())) {
            setVisibilityAllowed(!jobsStatusBean.isJobGroupDisabled(jobGroupRowName));
        } else {
            setVisibilityAllowed(jobsStatusBean.isJobGroupDisabled(jobGroupRowName));
        }
    }
}
