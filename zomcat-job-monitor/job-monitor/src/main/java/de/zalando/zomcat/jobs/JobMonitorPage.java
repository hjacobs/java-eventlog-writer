package de.zalando.zomcat.jobs;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.zalando.zomcat.HeartbeatMode;
import de.zalando.zomcat.OperationMode;
import de.zalando.zomcat.jobs.fragments.HeartbeatModeFragment;
import de.zalando.zomcat.jobs.fragments.JobFragment;
import de.zalando.zomcat.jobs.fragments.JobGroupModeFragment;
import de.zalando.zomcat.jobs.fragments.OperationModeFragment;
import de.zalando.zomcat.jobs.markup.helper.HoverOddEvenElement;
import de.zalando.zomcat.jobs.model.JobGroupRow;
import de.zalando.zomcat.jobs.model.JobMonitorForm;
import de.zalando.zomcat.jobs.model.JobRow;
import de.zalando.zomcat.monitoring.HeartbeatStatusBean;

public class JobMonitorPage extends WebPage {
    private static final long serialVersionUID = 2366951197279846029L;

    protected int total;

    @SpringBean
    private JobsStatusBean jobsStatusBean;

    @SpringBean
    private HeartbeatStatusBean heartbeatStatusBean;

    public JobMonitorPage() {
        final boolean enabled = getJobsStatusBean().getOperationModeAsEnum() == OperationMode.NORMAL;
        add(new OperationModeFragment(this, enabled));

        final boolean ok = heartbeatStatusBean.getHeartbeatModeAsEnum() == HeartbeatMode.OK;
        add(new HeartbeatModeFragment(this, ok));

        final Form<JobMonitorForm> form = new Form<JobMonitorForm>("form",
                new CompoundPropertyModel<JobMonitorForm>(new JobMonitorForm())) {
            private static final long serialVersionUID = 1L;
        };

        final CheckGroup<JobRow> group = new CheckGroup<JobRow>("group", form.getModelObject().getJobSelections());

        group.add(new CheckGroupSelector("groupselector"));
        form.add(group);

        final List<JobGroupRow> groupRows = form.getModelObject().getJobGroupRows(jobsStatusBean);
        final ListView<JobGroupRow> listview = new ListView<JobGroupRow>("jobGroupRow", groupRows) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<JobGroupRow> item) {
                final JobGroupRow jobGroupRow = item.getModelObject();
                item.add(new Label("jobGroupName", jobGroupRow.getGroupName()));

                item.add(new JobGroupModeFragment(this, jobGroupRow, jobsStatusBean));

                final List<JobRow> jobs = form.getModelObject().getJobRows(jobGroupRow, jobsStatusBean);

                item.add(new DataView<JobRow>("jobDetailRow", new ListDataProvider<JobRow>(jobs)) {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void populateItem(final Item<JobRow> item) {
                            item.add(new JobFragment(this, item, form.getModelObject()));
                        }

                        @Override
                        protected Item<JobRow> newItem(final String id, final int index, final IModel<JobRow> model) {
                            return new HoverOddEvenElement(id, index, model);
                        }
                    });
            }
        };

        // encapsulate the ListView in a WebMarkupContainer in order for it to update
        final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
        listContainer.setOutputMarkupId(true);
        listContainer.add(listview);

        group.add(new AjaxSubmitLink("enableSelected", form) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                    if (target != null) {
                        for (final JobRow jobTypeStatusBean : group.getModelObject()) {
                            jobsStatusBean.getJobTypeStatusBean(jobTypeStatusBean.getJobClass()).setDisabled(false);
                        }

                        target.add(listContainer);
                    }
                }

                @Override
                protected void onError(final AjaxRequestTarget target, final Form<?> form) { }
            });

        group.add(new AjaxSubmitLink("disableSelected", form) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                    if (target != null) {
                        for (final JobRow jobTypeStatusBean : group.getModelObject()) {
                            jobsStatusBean.getJobTypeStatusBean(jobTypeStatusBean.getJobClass()).setDisabled(true);
                        }

                        target.add(listContainer);
                    }
                }

                @Override
                protected void onError(final AjaxRequestTarget target, final Form<?> form) { }
            });

        group.add(listContainer);
        add(form);
    }

    public JobTypeStatusBean getJobTypeStatusBean(final Class<?> jobClass) {
        return jobsStatusBean.getJobTypeStatusBean(jobClass);
    }

    public HeartbeatStatusBean getHeartbeatStatusBean() {
        return heartbeatStatusBean;
    }

    public JobsStatusBean getJobsStatusBean() {
        return jobsStatusBean;
    }
}
