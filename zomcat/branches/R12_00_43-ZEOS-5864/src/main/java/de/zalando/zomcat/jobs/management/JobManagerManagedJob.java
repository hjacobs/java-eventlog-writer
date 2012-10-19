package de.zalando.zomcat.jobs.management;

import java.util.Date;

import org.quartz.JobDetail;
import org.quartz.Trigger;

import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import de.zalando.zomcat.jobs.RunningWorker;

public interface JobManagerManagedJob {

    /**
     * Getter for {@link JobSchedulingConfiguration}.
     *
     * @return  the {@link JobSchedulingConfiguration}
     */
    JobSchedulingConfiguration getJobSchedulingConfig();

    /**
     * Getter for {@link JobDetail}.
     *
     * @return  the Quartz {@link JobDetail}
     */
    JobDetail getQuartzJobDetail();

    /**
     * Getter for Quartz {@link Trigger}.
     *
     * @return  the Quartz {@link Trigger}
     */
    Trigger getQuartzTrigger();

    /**
     * Get non spring managed {@link SchedulerFactoryBean}.
     *
     * @return  The {@link SchedulerFactoryBean}
     */
    SchedulerFactoryBean getQuartzSchedulerFactoryBean();

    /**
     * Returns next Fire Time of Jobs associated Trigger.
     *
     * @return  next Run Date for Job
     */
    Date getNextJobRunDate();

    /**
     * Getter for Field: executionCount
     *
     * @return  the executionCount
     */
    int getExecutionCount();

    /**
     * Start a Running Worker.
     *
     * @param  runningWorker  The Running Worker that has been started for the Managed Job
     */
    void onStartRunningWorker(RunningWorker runningWorker);

    /**
     * Finish Running Worker.
     *
     * @param  runningWorker  The Running Woker that has finished for the Managed Job
     */
    void onFinishRunningWorker(RunningWorker runningWorker);

    /**
     * Return amount of running Workers for the Job.
     *
     * @return  The Amount of Running Workers
     */
    int getRunningWorkerCount();

}
