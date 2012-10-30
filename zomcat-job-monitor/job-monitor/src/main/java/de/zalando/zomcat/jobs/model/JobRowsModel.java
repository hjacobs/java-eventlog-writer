package de.zalando.zomcat.jobs.model;

import java.util.List;

import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.common.collect.Lists;

import de.zalando.zomcat.jobs.FinishedWorkerBean;
import de.zalando.zomcat.jobs.JobTypeStatusBean;
import de.zalando.zomcat.jobs.JobsStatusBean;

public class JobRowsModel extends BaseLoadableDetachableModel<List<JobRow>> {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private JobsStatusBean jobsStatusBean;

    private String jobGroupName;

    private JobRowsHistoryModel jobRowsHistoryModel;

    public JobRowsModel() {
        jobRowsHistoryModel = new JobRowsHistoryModel();
    }

    public JobRowsModel(final String jobGroupName, final JobRowsHistoryModel jobRowsHistoryModel) {
        this.jobGroupName = jobGroupName;
        this.jobRowsHistoryModel = jobRowsHistoryModel;
    }

    @Override
    protected List<JobRow> load() {

        final List<JobRow> returnList = Lists.newArrayList();
        for (final JobTypeStatusBean jobTypeStatusBean
                : jobsStatusBean.getJobTypeStatusBeansForGroup(jobGroupName, true)) {

            final Class<?> beanClass = jobTypeStatusBean.getJobClass();
            if (!jobRowsHistoryModel.getObject().containsKey(beanClass)) {
                jobRowsHistoryModel.put(beanClass, false);
            }

            // add the job itself:
            returnList.add(new JobRow(jobTypeStatusBean, jobRowsHistoryModel.getObject().get(beanClass)));

            // if there are any history entries, add them as well:
            if (jobRowsHistoryModel.getObject().get(beanClass)) {
                for (final FinishedWorkerBean finishedWorkerBean : jobTypeStatusBean.getHistory()) {
                    returnList.add(new JobRow(finishedWorkerBean));
                }
            }
        }

        return returnList;
    }

    public void toggleHistoryEnabled(final Class<?> clazz) {
        jobRowsHistoryModel.toggleHistoryEnabled(clazz);
    }

    public boolean isHistoryEnabled(final Class<?> clazz) {
        return jobRowsHistoryModel.isHistoryEnabled(clazz);
    }
}
