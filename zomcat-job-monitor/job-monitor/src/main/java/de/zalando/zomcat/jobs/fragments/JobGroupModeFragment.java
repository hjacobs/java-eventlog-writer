package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.markup.html.WebMarkupContainer;

import de.zalando.zomcat.jobs.model.JobGroupRow;
import de.zalando.zomcat.jobs.model.JobGroupRowModel;

public class JobGroupModeFragment extends WebMarkupContainer {
    private static final long serialVersionUID = 1L;

    public JobGroupModeFragment(final JobGroupRowModel jobGroupRowModel, final JobGroupRow jobGroupRow) {
        super("placeholderForJobGroupEnabled", jobGroupRowModel);

        add(new JobGroupMode("jobGroupEnabled", jobGroupRowModel, jobGroupRow));
        add(new JobGroupMode("jobGroupDisabled", jobGroupRowModel, jobGroupRow));
    }
}
