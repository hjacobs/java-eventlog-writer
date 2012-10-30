package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import de.zalando.zomcat.jobs.model.JobRow;
import de.zalando.zomcat.jobs.model.JobRowsModel;

public class JobModeFragment extends WebMarkupContainer {
    private static final long serialVersionUID = 1L;

    public JobModeFragment(final IModel<JobRow> jobRowModel, final JobRowsModel jobRowsModel) {
        super("placeholderForJobEnabled", jobRowsModel);

        setOutputMarkupPlaceholderTag(true);

        add(new JobMode("jobEnabled", jobRowModel, jobRowsModel));
        add(new JobMode("jobDisabled", jobRowModel, jobRowsModel));
        add(new JobMode("jobEnabledRowDisabled", jobRowModel, jobRowsModel));
        add(new JobMode("jobDisabledRowDisabled", jobRowModel, jobRowsModel));
        add(new JobMode("jobRowDisabled", jobRowModel, jobRowsModel));
    }
}
