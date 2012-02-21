package de.zalando.zomcat.jobs;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.CheckGroupSelector;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.zalando.zomcat.HeartbeatMode;
import de.zalando.zomcat.OperationMode;
import de.zalando.zomcat.monitoring.HeartbeatStatusBean;

public class JobMonitorPage extends WebPage {
    private static final long serialVersionUID = 2366951197279846029L;

    protected int total;

    @SpringBean
    private JobsStatusBean jobsStatusBean;

    @SpringBean
    private HeartbeatStatusBean heartbeatStatusBean;

    public JobMonitorPage() {

        final class OperationModeFragment extends Fragment {
            private static final long serialVersionUID = 1L;

            public OperationModeFragment(final MarkupContainer markupProvider) {
                super("placeholderForOperationMode",
                    jobsStatusBean.getOperationModeAsEnum() == OperationMode.NORMAL ? "operationModeNormal"
                                                                                    : "operationModeMaintenance",
                    markupProvider);

                setOutputMarkupPlaceholderTag(true);

                final AjaxLink<JobMonitorPage> operationModeToggleLink = new AjaxLink<JobMonitorPage>(
                        "operationModeToggle") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        jobsStatusBean.toggleOperationMode();

                        final OperationModeFragment toggledFragment = new OperationModeFragment(JobMonitorPage.this);
                        OperationModeFragment.this.replaceWith(toggledFragment);
                        target.add(toggledFragment);
                    }
                };

                add(operationModeToggleLink);
            }
        }

        final class HeartbeatModeFragment extends Fragment {
            private static final long serialVersionUID = 1L;

            public HeartbeatModeFragment(final MarkupContainer markupProvider) {
                super("placeholderForHeartbeatMode",
                    heartbeatStatusBean.getHeartbeatModeAsEnum() == HeartbeatMode.OK ? "hearbeatModeOk"
                                                                                     : "hearbeatModeDeploy",
                    markupProvider);

                setOutputMarkupPlaceholderTag(true);

                final AjaxLink<JobMonitorPage> heartbeatModeToggleLink = new AjaxLink<JobMonitorPage>(
                        "hearbeatModeToggle") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        heartbeatStatusBean.toggleHeartbeatMode();

                        final HeartbeatModeFragment toggledFragment = new HeartbeatModeFragment(JobMonitorPage.this);
                        HeartbeatModeFragment.this.replaceWith(toggledFragment);
                        target.add(toggledFragment);
                    }
                };

                add(heartbeatModeToggleLink);
            }
        }

        final class JobGroupModeFragment extends Fragment {
            private static final long serialVersionUID = 1L;

            public JobGroupModeFragment(final MarkupContainer markupProvider,
                    final JobGroupTypeStatusBean jobGroupTypeStatusBean) {
                super("placeholderForJobGroupEnabled",
                    jobGroupTypeStatusBean.isDisabled() ? "jobGroupDisabled" : "jobGroupEnabled", markupProvider);

                setOutputMarkupPlaceholderTag(true);

                final AjaxLink<JobMonitorPage> jobGroupModeToggleLink = new AjaxLink<JobMonitorPage>("jobGroupToggle") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        jobGroupTypeStatusBean.toggleMode();

                        final JobGroupModeFragment toggledFragment = new JobGroupModeFragment(markupProvider,
                                jobGroupTypeStatusBean);
                        JobGroupModeFragment.this.replaceWith(toggledFragment);
                        target.add(toggledFragment);
                    }
                };

                add(jobGroupModeToggleLink);
            }
        }

        final class JobModeFragment extends Fragment {
            private static final long serialVersionUID = 1L;

            public JobModeFragment(final MarkupContainer markupProvider, final JobTypeStatusBean jobTypeStatusBean) {
                super("placeholderForJobEnabled", jobTypeStatusBean.isDisabled() ? "jobDisabled" : "jobEnabled",
                    markupProvider);

                setOutputMarkupPlaceholderTag(true);

                final AjaxLink<JobMonitorPage> jobGroupModeToggleLink = new AjaxLink<JobMonitorPage>("jobToggle") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        jobTypeStatusBean.toggleMode();

                        final JobModeFragment toggledFragment = new JobModeFragment(markupProvider, jobTypeStatusBean);
                        JobModeFragment.this.replaceWith(toggledFragment);
                        target.add(toggledFragment);
                    }
                };

                add(jobGroupModeToggleLink);
            }
        }

        class FormBean {
            ListView<JobGroupTypeStatusBean> listview;

            public void setListview(final ListView<JobGroupTypeStatusBean> listview) {
                this.listview = listview;
            }

            public ListView<JobGroupTypeStatusBean> getListview() {
                return listview;
            }

        }

        add(new OperationModeFragment(this));
        add(new HeartbeatModeFragment(this));

        final CompoundPropertyModel<FormBean> formModel = new CompoundPropertyModel<FormBean>(new FormBean());

        final CheckGroup<JobTypeStatusBean> group = new CheckGroup<JobTypeStatusBean>("group",
                new ArrayList<JobTypeStatusBean>());

        final Form<FormBean> form = new Form<FormBean>("form") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit() { }
        };

        group.add(new CheckGroupSelector("groupselector"));
        form.add(group);

        final List<JobGroupTypeStatusBean> list = jobsStatusBean.getJobGroupTypeStatusBeans();
        final ListView<JobGroupTypeStatusBean> listview = new ListView<JobGroupTypeStatusBean>("jobGroupRow", list) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<JobGroupTypeStatusBean> item) {
                final JobGroupTypeStatusBean jobGroupTypeStatusBean = item.getModelObject();
                item.add(new Label("jobGroupName",
                        jobGroupTypeStatusBean.getJobGroupConfig() == null
                            ? "No Group" : jobGroupTypeStatusBean.getJobGroupConfig().getJobGroupName()));

                item.add(new JobGroupModeFragment(this, jobGroupTypeStatusBean));
                item.add(new DataView<JobTypeStatusBean>("jobDetailRow",
                        new ListDataProvider<JobTypeStatusBean>(
                            jobsStatusBean.getJobTypeStatusBeansForGroup(
                                jobGroupTypeStatusBean.getJobGroupConfig() == null
                                    ? null : jobGroupTypeStatusBean.getJobGroupConfig().getJobGroupName()))) {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void populateItem(final Item<JobTypeStatusBean> item) {
                            final JobTypeStatusBean jobTypeStatusBean = item.getModelObject();

                            item.add(new Check<JobTypeStatusBean>("checkbox", item.getModel()));

                            item.add(new AjaxLink<JobMonitorPage>("trigger") {
                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public void onClick(final AjaxRequestTarget target) { }
                                });

                            item.add(new JobModeFragment(this, jobTypeStatusBean));

                            item.add(new Label("name", jobTypeStatusBean.getJobClass().getSimpleName()));
                            item.add(new Label("description", jobTypeStatusBean.getDescription()));
                            item.add(new Label("workers", "" + jobTypeStatusBean.getRunningWorkers().size()));
                            item.add(new Label("timestamp", jobTypeStatusBean.getLastModifiedFormatted()));
                            item.add(new Label("historyButton", "+"));
                            item.add(
                                new Label("instances",
                                    jobTypeStatusBean.getJobConfig().getAllowedAppInstanceKeys().toString()));
                            item.add(new Label("batchSize", "" + jobTypeStatusBean.getJobConfig().getLimit()));
                            item.add(new AjaxLink<JobMonitorPage>("flowIdLink") {
                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public void onClick(final AjaxRequestTarget target) { }
                                });
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
                        for (final JobTypeStatusBean jobTypeStatusBean : group.getModelObject()) {
                            jobTypeStatusBean.setDisabled(false);
                            target.add(listContainer);
                        }
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
                        for (final JobTypeStatusBean jobTypeStatusBean : group.getModelObject()) {
                            jobTypeStatusBean.setDisabled(true);
                            target.add(listContainer);
                        }

                    }
                }

                @Override
                protected void onError(final AjaxRequestTarget target, final Form<?> form) { }
            });

        group.add(listContainer);
        add(form);

        /*
         * final List<Space> spaces = DashrService.getSpaces();
         *
         * add(new ListView<Space>("spaces", spaces) { private static final long
         * serialVersionUID = 1L;
         *
         * @Override protected void populateItem(final ListItem<Space> item) {
         * final Space space = item.getModelObject(); item.add(new
         * RowCollapsedPanel("rowPanel", space)); total +=
         * space.getTotalCount(); } }); add(new Label("total", new
         * PropertyModel<Integer>(this, "total")));
         */
    }
}
