package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;

import de.zalando.zomcat.jobs.JobMonitorPage;

public abstract class BaseFragment extends Fragment {

    public BaseFragment(final String id, final String markupId, final MarkupContainer markupProvider) {
        super(id, markupId, markupProvider);
    }

    private static final long serialVersionUID = -2592416450488804398L;

    protected JobMonitorPage getJobMonitorPage() {
        return (JobMonitorPage) getPage();
    }
}
