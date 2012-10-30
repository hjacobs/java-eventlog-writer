package de.zalando.zomcat.jobs;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.zalando.zomcat.jobs.fragments.HeartbeatModeFragment;
import de.zalando.zomcat.jobs.fragments.JobMonitorFragment;
import de.zalando.zomcat.jobs.fragments.OperationModeFragment;
import de.zalando.zomcat.jobs.model.HeartbeatModeModel;
import de.zalando.zomcat.jobs.model.JobMonitorModel;
import de.zalando.zomcat.jobs.model.OperationModeModel;
import de.zalando.zomcat.monitoring.HeartbeatStatusBean;

public class JobMonitorPage extends WebPage {
    private static final long serialVersionUID = 2366951197279846029L;

    protected int total;
    private boolean shouldRender = true;

    @SpringBean
    private JobsStatusBean jobsStatusBean;

    @SpringBean
    private HeartbeatStatusBean heartbeatStatusBean;

    private transient Gson gson;

    private OperationModeModel operationModeModel = new OperationModeModel();
    private HeartbeatModeModel heartbeatModeModel = new HeartbeatModeModel();
    private JobMonitorModel jobMonitorModel = new JobMonitorModel();

    @Override
    protected void onRender() {
        if (shouldRender) {
            super.onRender();
        }
    }

    public JobMonitorPage(final PageParameters parameters) {
        super(parameters);

        boolean processed = false;

        if ((parameters != null) && (parameters.getNamedKeys() != null) && parameters.getNamedKeys().contains("view")
                && parameters.getValues("view") != null) {
            for (final StringValue value : parameters.getValues("view")) {
                if ("json".equals(value.toString())) {
                    processed = true;
                    shouldRender = false;
                    RequestCycle.get().getOriginalResponse().write(getJson());
                    break;
                }
            }
        }

        if (!processed) {
            add(new OperationModeFragment("operationMode", operationModeModel));
            add(new HeartbeatModeFragment("hearbeatMode", heartbeatModeModel));
            add(new JobMonitorFragment("form", jobMonitorModel));
        }
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

    protected Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                                    .registerTypeAdapter(JobsStatusBean.class, new GsonJobsStatusBeanAdapter())
                                    .registerTypeAdapter(JobTypeStatusBean.class, new GsonJobTypeStatusBeanAdapter())
                                    .registerTypeAdapter(RunningWorkerBean.class, new GsonRunningWorkerBeanAdapter())
                                    .registerTypeAdapter(FinishedWorkerBean.class, new GsonFinishedWorkerBeanAdapter())
                                    .create();
        }

        return gson;
    }

    private String getJson() {
        return getGson().toJson(jobsStatusBean, JobsStatusBean.class);
    }
}
