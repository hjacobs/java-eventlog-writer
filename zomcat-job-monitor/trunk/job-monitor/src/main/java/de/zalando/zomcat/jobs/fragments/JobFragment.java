package de.zalando.zomcat.jobs.fragments;

import org.apache.log4j.Logger;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import org.springframework.web.context.ContextLoader;

import com.google.common.collect.Lists;

import de.zalando.zomcat.jobs.FinishedWorkerBean;
import de.zalando.zomcat.jobs.JobMonitorPage;
import de.zalando.zomcat.jobs.JobTypeStatusBean;
import de.zalando.zomcat.jobs.QuartzJobInfoBean;
import de.zalando.zomcat.jobs.RunningWorker;
import de.zalando.zomcat.jobs.model.JobMonitorForm;
import de.zalando.zomcat.jobs.model.JobRow;

public class JobFragment extends BaseFragment {
    private static final Logger LOG = Logger.getLogger(JobFragment.class);

    private static final String FLOWID_BASE_URL = "http://flowid.zalando.net/search/";
    private static final long serialVersionUID = 1L;

    public JobFragment(final MarkupContainer markupProvider, final Item<JobRow> item, final JobMonitorForm formModel) {
        super("placeholderForJob", item.getModelObject().isHistory() ? "jobHistory" : "simpleJob", markupProvider);

        final JobRow jobRow = item.getModelObject();

        if (jobRow.isHistory()) {
            final FinishedWorkerBean finishedWorkerBean = jobRow.getFinishedWorkerBean();
            add(new Label("startTime", finishedWorkerBean.getStartTimeFormatted()));
            add(new Label("endTime", finishedWorkerBean.getEndTimeFormatted()));
            add(new Label("workerId", String.valueOf(finishedWorkerBean.getId())));
            add(new Label("threadCPUNanoSeconds", String.valueOf(finishedWorkerBean.getThreadCPUNanoSeconds())));
            add(new Label("duration", finishedWorkerBean.getDuration()));

            final String href = FLOWID_BASE_URL + finishedWorkerBean.getFlowId();
            final ExternalLink flowIdLink = new ExternalLink("flowIdLink", href);
            flowIdLink.add(new Label("flowId", finishedWorkerBean.getFlowId()));
            add(flowIdLink);
        } else {
            add(new Check<JobRow>("checkbox", item.getModel()));
            add(new AjaxLink<JobMonitorPage>("trigger") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        final JobTypeStatusBean jobTypeStatusBean = getJobMonitorPage().getJobTypeStatusBean(
                                jobRow.getJobClass());
                        final QuartzJobInfoBean quartzJobInfoBean = jobTypeStatusBean.getQuartzJobInfoBean();
                        if (quartzJobInfoBean != null) {
                            final Scheduler scheduler = (Scheduler) ContextLoader.getCurrentWebApplicationContext()
                                                                                 .getBean(
                                                                                     quartzJobInfoBean
                                                                                             .getSchedulerName());
                            if (scheduler != null) {
                                try {
                                    scheduler.triggerJob(quartzJobInfoBean.getJobName(),
                                        quartzJobInfoBean.getJobGroup(), quartzJobInfoBean.getJobDataMap());
                                } catch (final SchedulerException e) {
                                    LOG.error("Could not trigger job: " + e.getMessage(), e);
                                }
                            }
                        }
                    }

                    @Override
                    public boolean isEnabled() {
                        return super.isEnabled()
                                && getJobMonitorPage().getJobTypeStatusBean(jobRow.getJobClass()).getQuartzJobInfoBean() != null;
                    }

                });

            final JobMonitorPage jobMonitorPage = (JobMonitorPage) markupProvider.getPage();

            final JobTypeStatusBean jobTypeStatusBean = jobMonitorPage.getJobTypeStatusBean(jobRow.getJobClass());
            add(new JobModeFragment(this, jobTypeStatusBean));

            add(new Label("name", jobTypeStatusBean.getJobClass().getSimpleName()));
            add(new Label("description", jobTypeStatusBean.getDescription()));
            add(new Label("workers", "" + jobTypeStatusBean.getRunningWorker()));
            add(new Label("timestamp", jobTypeStatusBean.getLastModifiedFormatted()));
            add(new HistoryModeFragment(this, jobRow, formModel));
            add(new Label("instances", jobTypeStatusBean.getJobConfig().getAllowedAppInstanceKeys().toString()));
            add(new Label("batchSize", "" + jobTypeStatusBean.getJobConfig().getLimit()));

            // TODO: add a serializable model for RunningWorker
            add(new ListView<RunningWorker>("jobFlowIds", Lists.newArrayList(jobTypeStatusBean.getRunningWorkers())) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void populateItem(final ListItem<RunningWorker> item) {
                        final RunningWorker modelObject = item.getModelObject();
                        final String href = FLOWID_BASE_URL + modelObject.getFlowId();
                        final ExternalLink flowIdLink = new ExternalLink("flowIdLink", href);
                        flowIdLink.add(new Label("flowId", modelObject.getFlowId()));
                        item.add(flowIdLink);
                    }
                });
        }
    }
}
