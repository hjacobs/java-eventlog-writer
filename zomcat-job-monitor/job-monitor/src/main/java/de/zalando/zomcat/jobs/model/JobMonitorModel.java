package de.zalando.zomcat.jobs.model;

public class JobMonitorModel extends BaseLoadableDetachableModel<JobMonitorForm> {
    private static final long serialVersionUID = 1L;

    private JobMonitorForm jobMonitorForm;

    @Override
    protected JobMonitorForm load() {

        if (jobMonitorForm == null) {
            jobMonitorForm = new JobMonitorForm();
        }

        return jobMonitorForm;
    }

}
