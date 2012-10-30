package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.markup.html.WebMarkupContainer;

import de.zalando.zomcat.jobs.behaviours.KeepAliveBehavior;
import de.zalando.zomcat.jobs.model.OperationModeModel;

public class OperationModeFragment extends WebMarkupContainer {
    private static final long serialVersionUID = 1L;

    public OperationModeFragment(final String id, final OperationModeModel operationModeModel) {
        super(id, operationModeModel);

        setOutputMarkupPlaceholderTag(true);
        add(new KeepAliveBehavior());
        add(new OperationMode("operationModeNormal", operationModeModel, de.zalando.zomcat.OperationMode.NORMAL));
        add(new OperationMode("operationModeMaintenance", operationModeModel,
                de.zalando.zomcat.OperationMode.MAINTENANCE));
    }
}
