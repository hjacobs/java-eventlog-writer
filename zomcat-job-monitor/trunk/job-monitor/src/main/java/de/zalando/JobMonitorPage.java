package de.zalando;

import java.util.List;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.zalando.zomcat.HeartbeatMode;
import de.zalando.zomcat.OperationMode;
import de.zalando.zomcat.jobs.JobGroupConfig;
import de.zalando.zomcat.jobs.JobTypeStatusBean;
import de.zalando.zomcat.jobs.JobsStatusBean;
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

        add(new OperationModeFragment(this));
        add(new HeartbeatModeFragment(this));

        final List<JobGroupConfig> list = jobsStatusBean.getJobGroupConfigs();
        final ListView<JobGroupConfig> listview = new ListView<JobGroupConfig>("jobGroupRow", list) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<JobGroupConfig> item) {
                final JobGroupConfig jobGroup = item.getModelObject();
                item.add(new Label("jobGroupName", jobGroup == null ? "No Group" : jobGroup.getJobGroupName()));
                item.add(new AjaxLink<JobMonitorPage>("jobGroupEnabled") {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void onClick(final AjaxRequestTarget target) { }
                    });
                item.add(new AjaxLink<JobMonitorPage>("jobGroupDisabled") {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void onClick(final AjaxRequestTarget target) { }
                    });

                item.add(new DataView<JobTypeStatusBean>("jobDetailRow",
                        new ListDataProvider<JobTypeStatusBean>(
                            jobsStatusBean.getJobTypeStatusBeansForGroup(
                                jobGroup == null ? null : jobGroup.getJobGroupName()))) {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void populateItem(final Item<JobTypeStatusBean> item) {
                            final JobTypeStatusBean jobTypeStatusBean = item.getModelObject();

                            item.add(new AjaxLink<JobMonitorPage>("trigger") {
                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public void onClick(final AjaxRequestTarget target) { }
                                });
                            item.add(new AjaxLink<JobMonitorPage>("pause") {
                                    private static final long serialVersionUID = 1L;

                                    @Override
                                    public void onClick(final AjaxRequestTarget target) { }
                                });
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
        add(listview);

/*        final List<Space> spaces = DashrService.getSpaces();
 *
 *      add(new ListView<Space>("spaces", spaces) {
 *              private static final long serialVersionUID = 1L;
 *
 *              @Override
 *              protected void populateItem(final ListItem<Space> item) {
 *                  final Space space = item.getModelObject();
 *                  item.add(new RowCollapsedPanel("rowPanel", space));
 *                  total += space.getTotalCount();
 *              }
 *          });
 *      add(new Label("total", new PropertyModel<Integer>(this, "total")));
 */
    }
}
