package de.zalando.zomcat.jobs.model;

import org.apache.wicket.spring.injection.annot.SpringBean;

import de.zalando.zomcat.OperationMode;
import de.zalando.zomcat.jobs.JobsStatusBean;

public class OperationModeModel extends BaseLoadableDetachableModel<OperationMode> {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private JobsStatusBean jobsStatusBean;

    @Override
    protected OperationMode load() {
        return jobsStatusBean.getOperationModeAsEnum();
    }

    public void toggle() {
        jobsStatusBean.toggleOperationMode();
    }
}
