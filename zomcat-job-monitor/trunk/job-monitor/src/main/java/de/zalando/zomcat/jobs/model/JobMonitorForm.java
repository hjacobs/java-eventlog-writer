package de.zalando.zomcat.jobs.model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.zalando.zomcat.jobs.FinishedWorkerBean;
import de.zalando.zomcat.jobs.JobGroupTypeStatusBean;
import de.zalando.zomcat.jobs.JobTypeStatusBean;
import de.zalando.zomcat.jobs.JobsStatusBean;

public class JobMonitorForm implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Class<?>, Boolean> jobStatusBeanShowHistoryMap = Maps.newHashMap();
    private final Map<String, List<JobRow>> jobTypeStatusBeanFinishedWorkerBeanModelList = Maps.newHashMap();
    private List<JobGroupRow> jobGroupRows = null;
    private final List<JobRow> jobSelections = new ArrayList<JobRow>();

    public JobMonitorForm() { }

    public void toggleShowHistory(final Class<?> jobClass) {
        Boolean entry = jobStatusBeanShowHistoryMap.get(jobClass);
        if (entry == null) {
            entry = Boolean.TRUE;
        } else {
            entry = !entry;
        }

        jobStatusBeanShowHistoryMap.put(jobClass, entry);
    }

    public boolean showHistory(final Class<?> jobClass) {
        final Boolean entry = jobStatusBeanShowHistoryMap.get(jobClass);
        if (entry == null) {
            return false;
        }

        return entry;
    }

    public List<JobRow> getJobRows(final JobGroupRow jobGroupRow, final JobsStatusBean jobsStatusBean) {

        // get a model or create one for the given group:
        List<JobRow> beanModels = jobTypeStatusBeanFinishedWorkerBeanModelList.get(jobGroupRow);
        if (beanModels == null) {
            beanModels = Lists.newArrayList();
            jobTypeStatusBeanFinishedWorkerBeanModelList.put(jobGroupRow.getGroupName(), beanModels);

            final List<JobTypeStatusBean> jobTypeStatusBeansForGroup = jobsStatusBean.getJobTypeStatusBeansForGroup(
                    jobGroupRow.getGroupName(), true);
            for (final JobTypeStatusBean jobTypeStatusBean : jobTypeStatusBeansForGroup) {
                beanModels.add(new JobRow(jobTypeStatusBean, showHistory(jobTypeStatusBean.getJobClass())));
            }
        }

        final List<JobRow> returnList = Lists.newArrayList();
        for (final JobRow jobTypeStatusBeanFinishedWorkerBeanModel : beanModels) {
            returnList.add(jobTypeStatusBeanFinishedWorkerBeanModel);

            final JobTypeStatusBean jobTypeStatusBean = jobsStatusBean.getJobTypeStatusBean(
                    jobTypeStatusBeanFinishedWorkerBeanModel.getJobClass());
            if (showHistory(jobTypeStatusBean.getJobClass())) {
                for (final FinishedWorkerBean finishedWorkerBean : jobTypeStatusBean.getHistory()) {
                    returnList.add(new JobRow(finishedWorkerBean));
                }
            }
        }

        return returnList;
    }

    public List<JobRow> getJobSelections() {
        return jobSelections;
    }

    public List<JobGroupRow> getJobGroupRows(final JobsStatusBean jobsStatusBean) {
        if (jobGroupRows == null) {
            jobGroupRows = Lists.newArrayList(Iterables.transform(jobsStatusBean.getJobGroupTypeStatusBeans(),
                        new Function<JobGroupTypeStatusBean, JobGroupRow>() {
                            @Override
                            public JobGroupRow apply(final JobGroupTypeStatusBean input) {
                                return new JobGroupRow(input.getJobGroupName());
                            }
                        }));
        }

        return jobGroupRows;
    }

    public void setJobGroupRows(final List<JobGroupRow> jobGroupRows) {
        this.jobGroupRows = jobGroupRows;
    }
}
