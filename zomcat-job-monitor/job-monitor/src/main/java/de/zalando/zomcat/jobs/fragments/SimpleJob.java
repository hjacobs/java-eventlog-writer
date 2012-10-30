package de.zalando.zomcat.jobs.fragments;

import java.util.List;

import org.apache.log4j.Logger;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.common.collect.Lists;

import de.zalando.zomcat.OperationMode;
import de.zalando.zomcat.jobs.JobMonitorPage;
import de.zalando.zomcat.jobs.JobTypeStatusBean;
import de.zalando.zomcat.jobs.JobsStatusBean;
import de.zalando.zomcat.jobs.RunningWorker;
import de.zalando.zomcat.jobs.model.BaseLoadableDetachableModel;
import de.zalando.zomcat.jobs.model.JobRow;
import de.zalando.zomcat.jobs.model.JobRowsModel;

public class SimpleJob extends WebMarkupContainer {

    private static final Logger LOG = Logger.getLogger(SimpleJob.class);

    private static final long serialVersionUID = 1L;

    private static final String FLOWID_BASE_URL = "http://flowid.zalando.net/zfg/flowid/";

    @SpringBean
    private JobsStatusBean jobsStatusBean;

    private Class<?> jobRowClass;
    private boolean isHistory;

    protected class TriggerBeanModel extends BaseLoadableDetachableModel<String> {
        private static final long serialVersionUID = 1L;
        private final Class<?> jobRowClass;

        public TriggerBeanModel(final Class<?> jobRowClass) {
            this.jobRowClass = jobRowClass;
        }

        @Override
        protected String load() {
            final JobTypeStatusBean jobTypeStatusBean = jobsStatusBean.getJobTypeStatusBean(jobRowClass);
            final boolean enabled = jobTypeStatusBean.getJobConfig().isActive()
                    && jobsStatusBean.getOperationModeAsEnum() == OperationMode.NORMAL;
            return enabled == true ? "Trigger" : "Disabled";
        }
    }

    protected class RunningWorkerBean {
        private final Integer actualProcessedItemNumber;
        private final Integer totalNumberOfItemsToBeProcessed;
        private String flowId;

        public RunningWorkerBean(final RunningWorker runningWorker) {
            this.actualProcessedItemNumber = runningWorker.getActualProcessedItemNumber();
            this.totalNumberOfItemsToBeProcessed = runningWorker.getTotalNumberOfItemsToBeProcessed();
            this.flowId = runningWorker.getFlowId();
        }

        public Integer getActualProcessedItemNumber() {
            return actualProcessedItemNumber;
        }

        public Integer getTotalNumberOfItemsToBeProcessed() {
            return totalNumberOfItemsToBeProcessed;
        }

        public String getFlowId() {
            return flowId;
        }

    }

    protected class RunningWorkerBeanModel extends BaseLoadableDetachableModel<List<RunningWorkerBean>> {
        private static final long serialVersionUID = 1L;
        private final Class<?> jobRowClass;

        public RunningWorkerBeanModel(final Class<?> jobRowClass) {
            this.jobRowClass = jobRowClass;
        }

        @Override
        protected List<RunningWorkerBean> load() {
            final List<RunningWorkerBean> ret = Lists.newArrayList();
            for (final RunningWorker runningWorker
                    : jobsStatusBean.getJobTypeStatusBean(jobRowClass).getRunningWorkers()) {
                ret.add(new RunningWorkerBean(runningWorker));
            }

            return ret;
        }
    }

    public SimpleJob(final IModel<JobRow> jobRowModel, final JobRowsModel jobRowsModel) {
        super("simpleJob", jobRowsModel);

        final JobRow jobRow = jobRowModel.getObject();
        this.jobRowClass = jobRow.getJobClass();
        isHistory = jobRow.isHistory();
        if (!isHistory) {

            final JobTypeStatusBean jobTypeStatusBean = jobsStatusBean.getJobTypeStatusBean(jobRow.getJobClass());
            final boolean enabled = jobTypeStatusBean.getJobConfig().isActive()
                    && jobsStatusBean.getOperationModeAsEnum() == OperationMode.NORMAL;

            final Check<JobRow> check = new Check<JobRow>("checkbox", jobRowModel);
            check.setEnabled(enabled);
            add(check);

            add(new AjaxLink<JobMonitorPage>("trigger") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(final AjaxRequestTarget target) {

                        // Trigger Job via JobsStatusBean
                        try {
                            jobsStatusBean.triggerJob(jobRowClass.getName());
                        } catch (final Throwable e) {
                            LOG.debug("Could not trigger job: " + jobRowClass.getName(), e);
                        }

                        target.add(getPage().get("form:group:listContainer"));
                    }

                    @Override
                    public boolean isEnabled() {
                        final JobTypeStatusBean jobTypeStatusBean = jobsStatusBean.getJobTypeStatusBean(jobRowClass);

                        return super.isEnabled() && jobTypeStatusBean.getJobConfig().isActive()
                                && jobsStatusBean.getOperationModeAsEnum() == OperationMode.NORMAL
                                && jobTypeStatusBean.getQuartzJobInfoBean() != null;
                    }

                }.add(new Label("triggerLabel", new TriggerBeanModel(jobRowClass))));

            add(new JobModeFragment(jobRowModel, jobRowsModel));

            add(new Label("name", jobTypeStatusBean.getJobClass().getSimpleName()));
            add(new Label("description", jobTypeStatusBean.getDescription()));
            add(new Label("workers", String.valueOf(jobTypeStatusBean.getRunningWorker())));
            add(new Label("timestamp", jobTypeStatusBean.getLastModifiedFormatted()));
            add(new HistoryModeFragment(jobRowModel, jobRowsModel));
            add(new Label("instances", jobTypeStatusBean.getJobConfig().getAllowedAppInstanceKeys().toString()));
            add(new Label("batchSize", String.valueOf(jobTypeStatusBean.getJobConfig().getLimit())));

            final RunningWorkerBeanModel currentRunningWorker = new RunningWorkerBeanModel(jobRow.getJobClass());
            add(new ListView<RunningWorkerBean>("progress", currentRunningWorker) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void populateItem(final ListItem<RunningWorkerBean> item) {
                        final RunningWorkerBean modelObject = item.getModelObject();
                        item.add(
                            new Label("jobProgress",
                                String.valueOf(modelObject.getActualProcessedItemNumber()) + "/"
                                    + String.valueOf(modelObject.getTotalNumberOfItemsToBeProcessed())));
                    }
                });

            add(new ListView<RunningWorkerBean>("jobFlowIds", currentRunningWorker) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void populateItem(final ListItem<RunningWorkerBean> item) {
                        final RunningWorkerBean modelObject = item.getModelObject();
                        final String href = FLOWID_BASE_URL + modelObject.getFlowId();
                        final ExternalLink flowIdLink = new ExternalLink("flowIdLink", href);
                        flowIdLink.add(new Label("flowId", modelObject.getFlowId()));
                        item.add(flowIdLink);
                    }
                });
        } else {
            final Check<JobRow> check = new Check<JobRow>("checkbox", jobRowModel);
            add(check);
            add(new AjaxLink<JobMonitorPage>("trigger") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(final AjaxRequestTarget target) { }
                }.add(new Label("triggerLabel", new TriggerBeanModel(jobRowClass))));

            add(new JobModeFragment(jobRowModel, jobRowsModel));

            add(new Label("name", "none"));
            add(new Label("description", "none"));
            add(new Label("workers", "none"));
            add(new Label("timestamp", "none"));
            add(new HistoryModeFragment(jobRowModel, jobRowsModel));
            add(new Label("instances", "none"));
            add(new Label("batchSize", "none"));

            final RunningWorkerBeanModel currentRunningWorker = new RunningWorkerBeanModel(jobRow.getJobClass());
            add(new ListView<RunningWorkerBean>("progress", currentRunningWorker) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void populateItem(final ListItem<RunningWorkerBean> item) {
                        final RunningWorkerBean modelObject = item.getModelObject();
                        item.add(
                            new Label("jobProgress",
                                String.valueOf(modelObject.getActualProcessedItemNumber()) + "/"
                                    + String.valueOf(modelObject.getTotalNumberOfItemsToBeProcessed())));
                    }
                });

            add(new ListView<RunningWorkerBean>("jobFlowIds", currentRunningWorker) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void populateItem(final ListItem<RunningWorkerBean> item) {
                        final RunningWorkerBean modelObject = item.getModelObject();
                        final String href = FLOWID_BASE_URL + modelObject.getFlowId();
                        final ExternalLink flowIdLink = new ExternalLink("flowIdLink", href);
                        flowIdLink.add(new Label("flowId", modelObject.getFlowId()));
                        item.add(flowIdLink);
                    }
                });
        }
    }

    @Override
    protected void onConfigure() {
        setVisibilityAllowed(!isHistory);
    }
}
