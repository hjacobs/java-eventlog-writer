package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.IModel;

import de.zalando.zomcat.jobs.model.JobRow;
import de.zalando.zomcat.jobs.model.JobRowsModel;

public class JobHistory extends WebMarkupContainer {

    private static final String FLOWID_BASE_URL = "http://flowid.zalando.net/zfg/flowid/";
    private static final long serialVersionUID = 1L;

    private boolean isHistory;

    public JobHistory(final IModel<JobRow> jobRowModel, final JobRowsModel jobRowsModel) {
        super("jobHistory", jobRowsModel);

        final JobRow jobRow = jobRowModel.getObject();

        isHistory = jobRow.isHistory();
        if (jobRow.isHistory()) {
            add(new Label("startTime", jobRow.getStartTime()));
            add(new Label("endTime", jobRow.getEndTime()));
            add(new Label("duration", jobRow.getDuration()));
            add(new Label("workerId", String.valueOf(jobRow.getWorkerId())));

            final String href = FLOWID_BASE_URL + jobRow.getFlowId();
            final ExternalLink flowIdLink = new ExternalLink("flowIdLink2", href);
            flowIdLink.add(new Label("flowId2", jobRow.getFlowId()));
            add(flowIdLink);
        } else {

            // do nothing - not visible
            add(new Label("startTime", ""));
            add(new Label("endTime", ""));
            add(new Label("duration", ""));
            add(new Label("workerId", ""));

            final ExternalLink flowIdLink = new ExternalLink("flowIdLink2", "");
            flowIdLink.add(new Label("flowId2", jobRow.getFlowId()));
            add(flowIdLink);
        }
    }

    @Override
    protected void onConfigure() {
        setVisibilityAllowed(isHistory);
    }

}
