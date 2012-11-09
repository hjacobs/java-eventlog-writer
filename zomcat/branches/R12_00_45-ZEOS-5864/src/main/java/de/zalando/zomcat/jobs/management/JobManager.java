package de.zalando.zomcat.jobs.management;

import java.util.List;

import org.quartz.JobDetail;

import de.zalando.zomcat.OperationMode;
import de.zalando.zomcat.jobs.JobGroupConfig;

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

    String SPRING_BEAN_NAME = "jobManager";

    /**
     * Get all managed Jobs. Contains scheduled and unscheduled Jobs
     *
     * @return  List of all scheduled Jobs
     */
    List<JobManagerManagedJob> getManagedJobs();

    /**
     * Get all managed Jobs. Contains scheduled and unscheduled Jobs
     *
     * @return  List of all scheduled Jobs
     */
    List<JobManagerManagedJob> getManagedJobsByClass(Class<?> jobClass) throws JobManagerException;

    /**
     * Get all managed Jobs. Contains scheduled and unscheduled Jobs
     *
     * @return  List of all scheduled Jobs
     */
    List<JobGroupConfig> getManagedJobGroups();

    /**
     * Get all scheduled managed Jobs.
     *
     * @return  List of all scheduled Jobs
     */
    List<JobManagerManagedJob> getScheduledManagedJobs();

    /**
     * Get all unscheduled managed Jobs.
     *
     * @return  List of all unscheduled Jobs
     */
    List<JobManagerManagedJob> getUnscheduledManagedJobs();

    /**
     * Get {@link JobManagerManagedJob} by {@link JobSchedulingConfiguration}.
     *
     * @param   jobSchedulingConfiguration  The {@link JobSchedulingConfiguration} identifying the
     *                                      {@link JobManagerManagedJob} to get
     *
     * @return  matching {@link JobManagerManagedJob} or <code>null</code> if not found
     *
     * @throws  JobManagerException  if any unanticipated error occurs during getManagedJob
     */
    JobManagerManagedJob getManagedJob(JobSchedulingConfiguration jobSchedulingConfiguration)
        throws JobManagerException;

    /**
     * Get Managed Job for JobName and JobGroup.
     *
     * @param   quartzJobDetailName    - The Quartz {@link JobDetail} name
     * @param   quartzJobDetailGroup-  The Quartz {@link JobDetail} group
     *
     * @return  {@link JobManagerManagedJob} idenfified by {@link JobSchedulingConfiguration}
     *
     * @throws  JobManagerException  if either parameter is <code>null</code>
     */
    JobManagerManagedJob getManagedJob(String quartzJobDetailName, String quartzJobDetailGroup)
        throws JobManagerException;

    /**
     * Getter for Started Status of JobManager.
     *
     * @return  <code>true</code> if the JobManager has been successfully started via startup method, <code>false</code>
     *          otherwise
     */
    boolean isStarted();

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
     * @param   force                       Even if the Job is not scheduled on current Application Instance, it may be
     *                                      forced to trigger by use of this flag. <code>true</code> will trigger the
     *                                      Job regardless of its scheduled state.
     *
     * @throws  JobManagerException  if any error occurs
     */
    void triggerJob(JobSchedulingConfiguration jobSchedulingConfiguration, boolean force) throws JobManagerException;

    /**
     * Trigger a given Job.
     *
     * @param   jobSchedulingConfiguration  The {@link JobSchedulingConfiguration} identifying the Job to be triggered
     * @param   force                       Even if the Job is not scheduled on current Application Instance, it may be
     *                                      forced to trigger by use of this flag. <code>true</code> will trigger the
     *                                      Job regardless of its scheduled state.
     *
     * @throws  JobManagerException  if any error occurs
     */
    void triggerJob(String quartzJobDetailName, String quartzJobDetailGroup, boolean force) throws JobManagerException;

    /**
     * Toggles a given Job on the respective Application Instance. Toggling a job will cause it to run indefinitely
     * according to scheduling configuration.
     *
     * @param   jobSchedulingConfiguration  The {@link JobSchedulingConfiguration} identifying the Job to be triggered
     * @param   active                      Toggle Job into active (<code>true</code>) or inactive (<code>false</code>)
     *                                      state
     *
     * @throws  JobManagerException  if any error occurs
     */
    void toggleJob(JobSchedulingConfiguration jobSchedulingConfiguration, boolean active) throws JobManagerException;

    /**
     * Toggles a given JobGroup on the respective Application Instance.
     *
     * @param   jobGroupName  The name of the JobGroup to toggle
     *
     * @throws  JobManagerException  if any error occurs
     */
    void toggleJobGroup(String jobGroupName) throws JobManagerException;

    /**
     * Cancel a given Job.
     *
     * @param   jobSchedulingConfig  The {@link JobSchedulingConfiguration} identifying the {@link JobManagerManagedJob}
     *                               to cancel
     *
     * @throws  JobManagerException  if any unanticipated error occurs on cancel of Job
     */
    void cancelJob(JobSchedulingConfiguration jobSchedulingConfig) throws JobManagerException;

    /**
     * Cancel a given Job.
     *
     * @param   quartzJobDetailName   The Quartz {@link JobDetail} Name of Job
     * @param   quartzJobDetailGroup  The Quartz {@link JobDetail} Group of Job
     *
     * @throws  JobManagerException  if any unanticipated error occurs on cancel of Job
     */
    void cancelJob(String quartzJobDetailName, String quartzJobDetailGroup) throws JobManagerException;

    /**
     * Cancel all Jobs.
     *
     * @throws  JobManagerException  if any unanticipated error occurs
     */
    void cancelAllJobs() throws JobManagerException;

    /**
     * Get the OperationMode.
     *
     * @return  The {@link OperationMode}
     */
    OperationMode getOperationMode();

    /**
     * Getter for Maintenance Mode Status - whether it is active or not.
     *
     * @return  <code>true</code> if {@link OperationMode} is set to MAINTENANCE, <code>false</code> otherwise
     */
    boolean isMainanenceModeActive();

    /**
     * Is Job Scheduled.
     *
     * @param   jobName   Quartz {@link JobDetail} Name
     * @param   jobGroup  Qiuartz {@link JobDetail} Group
     *
     * @return  <code>true</code>if Job is scheduled, <code>false</code> otherwise
     */
    boolean isJobScheduled(String jobName, String jobGroup) throws JobManagerException;

    /**
     * Activate the Maintenance Mode in the JobManager - cancels/holds all future Job Executions Waits for Jobs to be
     * finished.
     *
     * @param   isMaintenanceMode  if <code>true</code> set {@link OperationMode} to MAINTENANCE, if <code>false</code>
     *                             set {@link OperationMode} to NORMAL.
     *
     * @throws  JobManagerException  if any unanticipated error occurs setting the Maintenance Mode
     */
    void setMaintenanceModeActive(boolean isMaintenanceMode) throws JobManagerException;

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
     * @throws  JobManagerException  if any unanticipated error occurs performing the {@link JobSchedulingConfiguration}
     *                               update and resulting reschedule of Jobs.
     */
    void updateJobSchedulingConfigurations() throws JobManagerException;
}
