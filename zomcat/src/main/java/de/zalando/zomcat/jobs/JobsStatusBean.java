package de.zalando.zomcat.jobs;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.ApplicationContext;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.zalando.zomcat.OperationMode;

/**
 * bean holding all informations about quartz jobs.
 *
 * @author  fbrick
 */
@ManagedResource(objectName = "Zalando:name=Jobs Status Bean")
@Component("jobsStatusBean")
public class JobsStatusBean implements JobsStatusMBean, Serializable {

    private static final Logger LOG = Logger.getLogger(JobsStatusBean.class);

    private static final long serialVersionUID = 4508534808396173005L;

    public static final String BEAN_NAME = "jobsStatusBean";

    private OperationMode operationMode = OperationMode.NORMAL;

    private final SortedMap<String, JobTypeStatusBean> jobs = new TreeMap<String, JobTypeStatusBean>();

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.JobsStatusMBean#toggleOperationMode()
     */
    @ManagedOperation(description = "Toggles between NORMAL and MAINTENANCE OperationMode")
    @Override
    public String toggleOperationMode() {
        if (OperationMode.NORMAL.equals(operationMode)) {
            operationMode = OperationMode.MAINTENANCE;
        } else {
            operationMode = OperationMode.NORMAL;
        }

        return operationMode.toString();
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.JobsStatusMBean#getOperationMode()
     */
    @ManagedOperation(description = "Returns the actual OperationMode")
    @Override
    public String getOperationMode() {
        if (operationMode == null) {
            return null;
        }

        return operationMode.toString();
    }

    public OperationMode getOperationModeAsEnum() {
        if (operationMode == null) {
            return null;
        }

        return operationMode;
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.JobsStatusMBean#setOperationMode(de.zalando.commons.backend.enumeration.OperationMode)
     */
    @Override
    public void setOperationMode(final OperationMode operationMode) {
        this.operationMode = operationMode;
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.JobsStatusMBean#setOperationMode(java.lang.String)
     */
    @ManagedOperation(description = "sets the new OperationMode")
    @Override
    public void setOperationMode(final String operationMode) {
        this.operationMode = OperationMode.valueOf(operationMode);
    }

    /**
     * increment number of running workers by 1 for given {@link RunningWorker RunningWorker}.
     *
     * @param  runningWorker  the {@link RunningWorker RunningWorker} of the job
     */
    public void incrementRunningWorker(final RunningWorker runningWorker, final QuartzJobInfoBean quartzJobInfoBean) {
        final JobTypeStatusBean jobTypeStatusBean = getJobTypeStatusBean(runningWorker);

        jobTypeStatusBean.incrementRunningWorker(runningWorker, quartzJobInfoBean);
    }

    private synchronized JobTypeStatusBean getJobTypeStatusBean(final RunningWorker runningWorker) {
        JobTypeStatusBean jobTypeStatusBean = jobs.get(runningWorker.getClass().getName());

        // check if we already know this job type
        if (jobTypeStatusBean == null) {
            jobTypeStatusBean = new JobTypeStatusBean(runningWorker.getClass(), runningWorker.getDescription(),
                    runningWorker.getJobConfig());

            jobs.put(runningWorker.getClass().getName(), jobTypeStatusBean);
        }

        return jobTypeStatusBean;
    }

    /**
     * decrement number of running workers by 1 for given {@link RunningWorker RunningWorker}.
     *
     * @param  runningWorker  the {@link RunningWorker RunningWorker} of the job
     */
    public void decrementRunningWorker(final RunningWorker runningWorker) {
        final JobTypeStatusBean jobTypeStatusBean = getJobTypeStatusBean(runningWorker);

        jobTypeStatusBean.decrementRunningWorker(runningWorker);
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.JobsStatusMBean#getJobTypeStatusBeans()
     */
    @Override
    public List<JobTypeStatusBean> getJobTypeStatusBeans() {
        return new ArrayList<JobTypeStatusBean>(jobs.values());
    }

    public JobTypeStatusBean getJobTypeStatusBean(final Class<?> jobClass) {
        return jobs.get(jobClass.getName());
    }

    /**
     * @param   jobName  the fully qualified class name of job
     *
     * @return  the {@link JobTypeStatusBean JobTypeStatusBean} of the job or <code>null</code> if not found
     */
    public JobTypeStatusBean getJobTypeStatusBean(final String jobName) {
        return jobs.get(jobName);
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.JobsStatusMBean#toggleJob(java.lang.String, boolean)
     */
    @ManagedOperation(
        description = "toggles job running mode and returns flag if successful or not, "
                + "if <code>null</code> is returned then an error occured, "
                + "then please look into logfile for details"
    )
    @Override
    public Boolean toggleJob(final String jobName, final boolean running) {
        final JobTypeStatusBean jobTypeStatusBean = getJobTypeStatusBean(jobName);

        if (jobTypeStatusBean == null) {
            LOG.info("job not found, status can not be toggled " + jobName);

            return null;
        } else {
            jobTypeStatusBean.setDisabled(!running);

            LOG.info("set enabled/disabled mode of job + " + jobName + ", now it is: " + !running);
        }

        return !jobTypeStatusBean.isDisabled();
    }

    /**
     * @param   jobName  the fully qualified job class name
     *
     * @return  flag if successful or not, if <code>null</code> then an error occured, please look into logfile for
     *          details
     */
    public Boolean toggleJob(final String jobName) {
        final JobTypeStatusBean statusBean = jobs.get(jobName);

        if (statusBean == null) {
            LOG.info("no job found with name jobName = " + jobName + ", so nothing is toggled!");

            return null;
        }

        statusBean.setDisabled(!statusBean.isDisabled());

        LOG.info("toggled job " + jobName + ", now it is: " + statusBean);

        return !statusBean.isDisabled();
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.JobsStatusMBean#getListOfJobTypeStatusBeans()
     */
    @ManagedOperation(description = "Returns the collection of JobTypeStatusBeans")
    @Override
    public List<Map<String, String>> getListOfJobTypeStatusBeans() {

        final Collection<JobTypeStatusBean> values = jobs.values();

        final ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

        if (values == null) {
            return list;
        }

        for (final JobTypeStatusBean bean : values) {
            list.add(bean.toMap());
        }

        return list;
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.JobsStatusMBean#getNumberOfDifferentJobTypes()
     */
    @ManagedOperation(description = "Returns the size of different job types")
    @Override
    public int getNumberOfDifferentJobTypes() {
        return jobs.size();
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.JobsStatusMBean#getTotalNumberOfRunningWorkers()
     */
    @ManagedOperation(description = "Returns the total number of running workers")
    @Override
    public int getTotalNumberOfRunningWorkers() {
        int total = 0;

        for (final JobTypeStatusBean jobTypeStatusBean : jobs.values()) {
            total += jobTypeStatusBean.getRunningWorker();
        }

        return total;
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.JobsStatusMBean#triggerJob(java.lang.String)
     */
    @ManagedOperation(
        description = "trigger job with given fully qualified job class name. "
                + "Returns flag if successful or not. If not successful then look into logs for more detailed information."
    )
    @Override
    public boolean triggerJob(final String jobName) {
        if (jobName == null) {
            LOG.info("jobName == null => no job is triggered");

            return false;
        }

        final JobTypeStatusBean jobTypeStatusBean = getJobTypeStatusBean(jobName);

        if (jobTypeStatusBean == null) {
            LOG.info("job " + jobName + " not found, job is not triggered");

            return false;
        }

        final QuartzJobInfoBean lastQuartzJobInfoBean = jobTypeStatusBean.getLastQuartzJobInfoBean();

        if (lastQuartzJobInfoBean == null) {
            LOG.info("lastQuartzJobInfoBean not found for job " + jobName + ", job can not be triggered");

            return false;
        }

        final Scheduler scheduler = (Scheduler) applicationContext.getBean(lastQuartzJobInfoBean.getSchedulerName());

        if (scheduler == null) {
            LOG.info("scheduler " + lastQuartzJobInfoBean.getSchedulerName() + " not found for job " + jobName
                    + ", lastQuartzJobInfoBean = " + lastQuartzJobInfoBean + ", job can not be triggered");

            return false;
        }

        LOG.info("starting triggering job " + jobName + " with lastQuartzJobInfoBean = " + lastQuartzJobInfoBean
                + " ...");

        try {
            scheduler.triggerJob(lastQuartzJobInfoBean.getJobName(), lastQuartzJobInfoBean.getJobGroup());
        } catch (final SchedulerException e) {
            LOG.error("failed to trigger job " + jobName + " with lastQuartzJobInfoBean = " + lastQuartzJobInfoBean, e);

            return false;
        }

        LOG.info("... finished triggering job " + jobName + " with lastQuartzJobInfoBean = " + lastQuartzJobInfoBean);

        return true;
    }

    /**
     * @see  java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("JobsStatusBean [jobs=");
        builder.append(jobs);
        builder.append(", operationMode=");
        builder.append(operationMode);
        builder.append("]");
        return builder.toString();
    }

    public List<JobGroupConfig> getJobGroups() {
        return Lists.newArrayList();
// return Lists.newArrayList(new JobGroupConfig("docDataJobs", true, Sets.newHashSet("123")),

// new JobGroupConfig("partnerJobs", true, Sets.newHashSet("345")));
    }

    public List<JobTypeStatusBean> getJobTypeStatusBeansForGroup(final String jobGroupName) {
        return Lists.newArrayList(Iterables.filter(getJobTypeStatusBeans(), new Predicate<JobTypeStatusBean>() {
                        @Override
                        public boolean apply(final JobTypeStatusBean input) {
                            if (StringUtils.isEmpty(jobGroupName)) {

                                // filter this one if getJobGroupConfig() != null
                                return input.getJobConfig().getJobGroupConfig() != null;
                            }

                            if (input.getJobConfig().getJobGroupConfig() == null) {

                                // filter this one
                                return true;
                            }

                            return jobGroupName.equals(input.getJobConfig().getJobGroupConfig().getJobGroupName());
                        }
                    }));
    }
}
