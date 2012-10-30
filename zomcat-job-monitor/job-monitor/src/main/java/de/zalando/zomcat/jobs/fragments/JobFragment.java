package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import de.zalando.zomcat.jobs.model.JobRow;
import de.zalando.zomcat.jobs.model.JobRowsModel;

public class JobFragment extends WebMarkupContainer {

    private static final long serialVersionUID = 1L;

    public JobFragment(final IModel<JobRow> jobRowModel, final JobRowsModel jobRowsModel) {
        super("placeholderForJob", jobRowsModel);

        setOutputMarkupPlaceholderTag(true);

        add(new SimpleJob(jobRowModel, jobRowsModel));
        add(new JobHistory(jobRowModel, jobRowsModel));
    }
}
