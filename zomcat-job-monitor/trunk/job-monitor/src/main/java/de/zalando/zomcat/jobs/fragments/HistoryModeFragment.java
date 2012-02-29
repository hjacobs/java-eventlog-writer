package de.zalando.zomcat.jobs.fragments;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;

import de.zalando.zomcat.jobs.JobMonitorPage;
import de.zalando.zomcat.jobs.model.JobMonitorForm;
import de.zalando.zomcat.jobs.model.JobRow;

public class HistoryModeFragment extends BaseFragment {
    private static final long serialVersionUID = 1L;

    public HistoryModeFragment(final JobFragment markupProvider, final JobRow jobRow, final JobMonitorForm formModel) {
        super("placeholderForHistory",
            formModel.showHistory(jobRow.getJobClass()) ? "historyEnabled" : "historyDisabled", markupProvider);

        final AjaxLink<JobMonitorPage> jobGroupModeToggleLink = new AjaxLink<JobMonitorPage>("historyToggle") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(final AjaxRequestTarget target) {
                formModel.toggleShowHistory(jobRow.getJobClass());
// target.add(markupProvider.getParent().getParent().getParent().getParent().getParent());
                target.add(getPage().get("form:group:listContainer"));
            }
        };

        add(jobGroupModeToggleLink);
    }
}
