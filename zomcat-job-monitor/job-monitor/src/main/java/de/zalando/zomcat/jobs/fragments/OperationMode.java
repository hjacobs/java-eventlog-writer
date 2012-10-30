package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;

import de.zalando.zomcat.jobs.JobMonitorPage;
import de.zalando.zomcat.jobs.model.OperationModeModel;

public class OperationMode extends WebMarkupContainer {
    private static final long serialVersionUID = 1L;

    private de.zalando.zomcat.OperationMode operationMode;

    public OperationMode(final String id, final OperationModeModel operationModeModel,
            final de.zalando.zomcat.OperationMode normal) {
        super(id, operationModeModel);

        this.operationMode = normal;

        final AjaxLink<JobMonitorPage> operationModeToggleLink = new AjaxLink<JobMonitorPage>("operationModeToggle") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(final AjaxRequestTarget target) {
                operationModeModel.toggle();
                target.add(getParent().getParent());
                target.add(getPage().get("form:group:listContainer"));
            }
        };

        add(operationModeToggleLink);
    }

    @Override
    protected void onConfigure() {
        setVisibilityAllowed(getDefaultModelObject().equals(operationMode));
    }
}
