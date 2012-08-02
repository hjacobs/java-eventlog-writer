package de.zalando.zomcat.jobs.management;

import java.util.List;

import org.quartz.JobDetail;
import org.quartz.Trigger;

/**
 * Manager Component for Jobs being run in zomcat based application. Configuration is provided by respective
 * {@link JobSchedulingConfigurationProvider} implementation associated with the manager implementation. Default
 * {@link JobSchedulingConfiguration} source is a configuration file 'scheduler.conf' expected to be within the
 * classpath. Currently two types of Jobs are supported - SIMPLE Jobs - running initially after configured delay and
 * repetative after configured interval. Also supported are CRON Jobs that run according to configured CRON Expression.
 * Example configuration:
 *
 * <p>every 30s after 10s de.zalando.orderengine.backend.jobs.ProcessPaymentNotificationsJob</p>
 *
 * <p>cron 0 30 2 * * ? de.zalando.partner.backend.jobs.CleanQueuesJob partnerId=3001</p>
 *
 * Lines starting with # (comments) and empty lines are ignored in scheduler.conf.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public interface JobManager {

    /**
     * Get all Scheduled Jobs.
     *
     * @return  List of all scheduled Jobs
     */
    List<JobManagerManagedJob> getManagedJobs();

    /**
     * Schedule a single Job.
     *
     * @param   jobSchedulingConfig  The Job Scheduling Config
     *
     * @throws  JobManagerException  if any error occurs during scheduling
     */
    void scheduleJob(JobSchedulingConfiguration jobSchedulingConfig) throws JobManagerException;

    /**
     * Reschedule a single Job.
     *
     * @param   jobSchedulingConfig  The Job Scheduling Config
     *
     * @throws  JobManagerException  if any error occurs during scheduling
     */
    void rescheduleJob(JobSchedulingConfiguration jobSchedulingConfig) throws JobManagerException;

    /**
     * Trigger a given Job.
     *
     * @param   jobSchedulingConfiguration  The {@link JobSchedulingConfiguration} identifying the Job to be triggered
     *
     * @throws  JobManagerException  if any error occurs
     */
    void triggerJob(JobSchedulingConfiguration jobSchedulingConfiguration) throws JobManagerException;

    /**
     * Trigger a given Job.
     *
     * @param   jobSchedulingConfiguration  The {@link JobSchedulingConfiguration} identifying the Job to be triggered
     *
     * @throws  JobManagerException  if any error occurs
     */
    void triggerJob(JobDetail quartzJobDetail) throws JobManagerException;

    /**
     * Trigger a given Job.
     *
     * @param   jobSchedulingConfiguration  The {@link JobSchedulingConfiguration} identifying the Job to be triggered
     *
     * @throws  JobManagerException  if any error occurs
     */
    void triggerJob(Trigger quartzTrigger) throws JobManagerException;

    /**
     * Trigger a given Job.
     *
     * @param   jobSchedulingConfiguration  The {@link JobSchedulingConfiguration} identifying the Job to be triggered
     *
     * @throws  JobManagerException  if any error occurs
     */
    void triggerJob(String quartzJobDetailName, String quartzJobDetailGroup) throws JobManagerException;

    /**
     * Cancel a given Job.
     *
     * @param   jobSchedulingConfig
     *
     * @throws  JobManagerException
     */
    void cancelJob(JobSchedulingConfiguration jobSchedulingConfig) throws JobManagerException;

    /**
     * Cancel all Jobs.
     *
     * @throws  JobManagerException  if any unanticipated error occurs
     */
    void cancelAllJobs() throws JobManagerException;

    /**
     * Getter for Maintanence Mode Status - whether it is active or not.
     *
     * @param  isMaintanenceMode
     */
    boolean isMainanenceModeActive();

    /**
     * Activate the Maintanence Mode in the JobManager - cancels/holds all future Job Executions Waits for Jobs to be
     * finished.
     *
     * @param  isMaintanenceMode
     */
    void setMainanenceModeActive(boolean isMaintanenceMode) throws JobManagerException;

    /**
     * Simple Startup Method for JobManager.
     *
     * @throws  JobManagerException  if Startup fails for any reason
     */
    void startup() throws JobManagerException;

    /**
     * Simple Shutdown Method for JobManager.
     *
     * @throws  JobManagerException  if Shutdown fails for any reason
     */
    void shutdown() throws JobManagerException;

    /**
     * Cause all {@link JobSchedulingConfiguration}s to be updated and Jobs rescheduled as necessary.
     *
     * @throws  JobManagerException
     */
    void updateJobSchedulingConfigurations() throws JobManagerException;
}
