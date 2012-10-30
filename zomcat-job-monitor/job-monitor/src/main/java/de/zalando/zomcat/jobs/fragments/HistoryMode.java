package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import de.zalando.zomcat.jobs.JobMonitorPage;
import de.zalando.zomcat.jobs.model.JobRow;
import de.zalando.zomcat.jobs.model.JobRowsModel;

public class HistoryMode extends WebMarkupContainer {
    private static final long serialVersionUID = 1L;

    private Class<?> clazz;

    public HistoryMode(final String id, final IModel<JobRow> jobRowModel, final JobRowsModel jobRowsModel) {
        super(id, jobRowsModel);

        this.clazz = jobRowModel.getObject().getJobClass();

        final AjaxLink<JobMonitorPage> jobGroupModeToggleLink = new AjaxLink<JobMonitorPage>("historyToggle") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(final AjaxRequestTarget target) {
                jobRowsModel.toggleHistoryEnabled(clazz);
                target.add(getPage().get("form:group:listContainer"));
            }
        };

        add(jobGroupModeToggleLink);
    }

    @Override
    protected void onConfigure() {
        final JobRowsModel jobRowsModel = (JobRowsModel) getDefaultModel();
        boolean enabled;
        if ("historyEnabled".equals(getId())) {
            enabled = jobRowsModel.isHistoryEnabled(clazz);
        } else {
            enabled = !jobRowsModel.isHistoryEnabled(clazz);
        }

        setVisibilityAllowed(enabled);
    }

}
