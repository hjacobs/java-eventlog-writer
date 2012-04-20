package de.zalando.zomcat.jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import org.quartz.impl.SchedulerRepository;

import org.reflections.Reflections;

import org.reflections.scanners.SubTypesScanner;

import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.context.ApplicationContext;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import org.springframework.stereotype.Component;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.zalando.zomcat.OperationMode;

/**
 * Bean holding all informations about quartz jobs.
 *
 * @author  fbrick
 */
@ManagedResource(objectName = "Zalando:name=Jobs Status Bean")
@Component("jobsStatusBean")
public class JobsStatusBean implements JobsStatusMBean {

    private static final Logger LOG = Logger.getLogger(JobsStatusBean.class);

    public static final String BEAN_NAME = "jobsStatusBean";

    private OperationMode operationMode = OperationMode.NORMAL;

    private final SortedMap<String, JobTypeStatusBean> jobs = new TreeMap<String, JobTypeStatusBean>();
    private final SortedMap<String, JobGroupTypeStatusBean> groups = new TreeMap<String, JobGroupTypeStatusBean>();

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * required = false covers the cases where a component does not have any jobs and thus does not define any
     * "jobConfigSource" bean. Otherwise, if the component has jobs, this will later properly fail (probably with a
     * NPE), meaning still that IF JOBS ARE IN USE THE COMPONENT MUST DEFINE A "jobConfigSource" bean.
     */
    @Autowired(required = false)
    @Qualifier("applicationConfig")
    private JobConfigSource jobConfigSource;

    public SortedMap<String, JobTypeStatusBean> getJobs() {
        if (jobs.isEmpty()) {
            synchronized (this) {
                if (jobs.isEmpty()) {
                    final Reflections reflections = new Reflections(new ConfigurationBuilder().filterInputsBy(
                                new FilterBuilder.Include(FilterBuilder.prefix("de.zalando"))).setUrls(
                                ClasspathHelper.forPackage("de.zalando")).setScanners(new SubTypesScanner()));

                    final Set<Class<? extends RunningWorker>> runningWorkerImplementations = reflections.getSubTypesOf(
                            RunningWorker.class);
                    for (final Class<? extends RunningWorker> runningWorkerClass : runningWorkerImplementations) {
                        try {
                            final RunningWorker runningWorker = runningWorkerClass.newInstance();

                            if (runningWorker instanceof AbstractJob) {
                                final AbstractJob abstractJob = (AbstractJob) runningWorker;

                                // make sure we can use the spring context in the abstract job
                                abstractJob.setApplicationContext(applicationContext);
                            }

                            final JobTypeStatusBean jobTypeStatusBean = new JobTypeStatusBean(runningWorker.getClass(),
                                    runningWorker.getDescription(), runningWorker.getJobConfig(),
                                    getQuartzJobStatusBean(runningWorker.getClass()));
                            jobs.put(runningWorkerClass.getName(), jobTypeStatusBean);
                        } catch (final Exception e) {
                            // ignore
                        }
                    }
                }
            }
        }

        return jobs;
    }

    private QuartzJobInfoBean getQuartzJobStatusBean(final Class<?> jobClazz) {
        @SuppressWarnings("unchecked")
        final Collection<Scheduler> lookupAll = SchedulerRepository.getInstance().lookupAll();
        for (final Scheduler s : lookupAll) {

            String[] jobGroupNames = null;
            try {
                jobGroupNames = s.getJobGroupNames();
            } catch (final SchedulerException e1) { }

            for (final String jobGroupName : jobGroupNames) {
                try {
                    final String[] jobNames = s.getJobNames(jobGroupName);
                    for (final String jobName : jobNames) {
                        final JobDetail jobDetail = s.getJobDetail(jobName, jobGroupName);
                        if (jobDetail.getJobClass().equals(jobClazz)) {

                            // job found.
                            final String schedulerName = s.getSchedulerName();
                            final JobDataMap jobDataMap = jobDetail.getJobDataMap();
                            return new QuartzJobInfoBean(schedulerName, jobName, jobGroupName, jobDataMap);
                        }
                    }
                } catch (final SchedulerException e) {
                    LOG.error("Could not extraxr jobDetais: " + e.getMessage(), e);
                }
            }
        }

        return null;
    }

    private Map<String, JobGroupTypeStatusBean> getJobGroups() {
        if (groups.isEmpty()) {
            synchronized (this) {
                if (groups.isEmpty()) {
                    if (jobs.isEmpty()) {
                        getJobs();
                    }

                    // now get all job group configs and create JobGroupTypeStatusBeans:
                    for (final JobGroupConfig jobGroupConfig : getJobGroupConfigs()) {
                        groups.put(jobGroupConfig == null ? JobGroupConfig.DEFAULT_GROUP_NAME
                                                          : jobGroupConfig.getJobGroupName(),
                            new JobGroupTypeStatusBean(jobGroupConfig));
                    }
                }
            }
        }

        return groups;
    }

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
        final JobTypeStatusBean jobTypeStatusBean = getJobs().get(runningWorker.getClass().getName());

        // check if we already know this job type
        if (jobTypeStatusBean == null) {
            throw new RuntimeException("job must be well known at this time: " + runningWorker.getClass().getName());
        }

        return jobTypeStatusBean;
    }

    public synchronized JobGroupTypeStatusBean getJobGroupTypeStatusBean(final JobConfig jobConfig) {
        final JobGroupTypeStatusBean jobGroupTypeStatusBean = getJobGroups().get(jobConfig.getJobGroupName());

        // check if we already know this job type
        if (jobGroupTypeStatusBean == null) {
            throw new RuntimeException("job group must be well known at this time: " + jobConfig);
        }

        return jobGroupTypeStatusBean;
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
        return Lists.newArrayList(getJobs().values());
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.JobsStatusMBean#getJobTypeStatusBeans()
     */
    public List<JobTypeStatusBean> getJobTypeStatusBeans(final boolean filterByAppInstanceKey) {
        if (filterByAppInstanceKey == false) {
            return getJobTypeStatusBeans();
        }

        final String localAppinstanceKey = jobConfigSource.getAppInstanceKey();
        return Lists.newArrayList(Iterables.filter(getJobs().values(), new Predicate<JobTypeStatusBean>() {
                        @Override
                        public boolean apply(final JobTypeStatusBean input) {
                            return input.getJobConfig().isAllowedAppInstanceKey(localAppinstanceKey);
                        }
                    }));
    }

    @Override
    public List<JobGroupTypeStatusBean> getJobGroupTypeStatusBeans() {
        return Lists.newArrayList(getJobGroups().values());
    }

    public JobTypeStatusBean getJobTypeStatusBean(final Class<?> jobClass) {
        return getJobs().get(jobClass.getName());
    }

    /**
     * @param   jobName  the fully qualified class name of job
     *
     * @return  the {@link JobTypeStatusBean JobTypeStatusBean} of the job or <code>null</code> if not found
     */
    public JobTypeStatusBean getJobTypeStatusBean(final String jobName) {
        return getJobs().get(jobName);
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
     * @see  de.zalando.commons.backend.domain.monitoring.JobsStatusMBean#toggleJob(java.lang.String, boolean)
     */
    @ManagedOperation(
        description = "toggles job group running mode and returns flag if successful or not, "
                + "if <code>null</code> is returned then an error occured, "
                + "then please look into logfile for details"
    )
    @Override
    public Boolean toggleJobGroup(final String groupName) {
        final JobGroupTypeStatusBean jobGroupTypeStatusBean = groups.get(groupName);

        if (jobGroupTypeStatusBean == null) {
            LOG.info("job group not found, status can not be toggled " + groupName);

            return null;
        } else {
            jobGroupTypeStatusBean.toggleMode();

            LOG.info("set enabled/disabled mode of job group + " + groupName + ", now it is: "
                    + !jobGroupTypeStatusBean.isDisabled());
        }

        return !jobGroupTypeStatusBean.isDisabled();
    }

    /**
     * @param   jobName  the fully qualified job class name
     *
     * @return  flag if successful or not, if <code>null</code> then an error occured, please look into logfile for
     *          details
     */
    public Boolean toggleJob(final String jobName) {
        final JobTypeStatusBean statusBean = getJobs().get(jobName);

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

        final Collection<JobTypeStatusBean> values = getJobs().values();

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
        return getJobs().size();
    }

    /**
     * @see  de.zalando.commons.backend.domain.monitoring.JobsStatusMBean#getTotalNumberOfRunningWorkers()
     */
    @ManagedOperation(description = "Returns the total number of running workers")
    @Override
    public int getTotalNumberOfRunningWorkers() {
        int total = 0;

        for (final JobTypeStatusBean jobTypeStatusBean : getJobs().values()) {
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

        final QuartzJobInfoBean lastQuartzJobInfoBean = jobTypeStatusBean.getQuartzJobInfoBean();

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
        builder.append(getJobs());
        builder.append(", operationMode=");
        builder.append(operationMode);
        builder.append("]");
        return builder.toString();
    }

    private Set<JobGroupConfig> getJobGroupConfigs() {
        final Set<JobGroupConfig> set = new TreeSet<JobGroupConfig>(new Comparator<JobGroupConfig>() {
                    @Override
                    public int compare(final JobGroupConfig o1, final JobGroupConfig o2) {
                        if (o1 == null && o2 == null) {
                            return 0;
                        }

                        if (o1 == null && o2 != null) {
                            return 1;
                        }

                        if (o1 != null && o2 == null) {
                            return -1;
                        }

                        return o1.getJobGroupName().compareTo(o2.getJobGroupName());
                    }
                });

        for (final JobTypeStatusBean jobTypeStatusBean : getJobs().values()) {
            final JobGroupConfig jobGroupConfig = jobTypeStatusBean.getJobConfig().getJobGroupConfig();
            set.add(jobGroupConfig);
        }

        return set;
    }

    public boolean isJobGroupDisabled(final String groupName) {
        return groups.get(groupName).isDisabled();
    }

    public List<JobTypeStatusBean> getJobTypeStatusBeansForGroup(final String jobGroupName,
            final boolean filterByAppInstanceKey) {
        return Lists.newArrayList(Iterables.filter(getJobTypeStatusBeans(filterByAppInstanceKey),
                    new Predicate<JobTypeStatusBean>() {
                        @Override
                        public boolean apply(final JobTypeStatusBean input) {
                            if (StringUtils.isEmpty(jobGroupName)) {

                                // filter this one if getJobGroupConfig() != null
                                return input.getJobConfig().getJobGroupName().equals(JobGroupConfig.DEFAULT_GROUP_NAME);
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
