package de.zalando.zomcat.jobs;

import org.apache.log4j.Logger;

import org.quartz.JobExecutionContext;

/**
 * Abstract Job that depends on the {@link JobConfig} in order to be initialized properly. Jobs of this type are passed
 * the {@link JobConfig} instance in their doRun method. Majority of Job Initialization is taken care of in this Base
 * Class (Allowed AppInstanceKeys, Active State, Processing Limit for Items processed by the Job if there is a limit)
 * This class is meant to be extended in applications (e.g. in the Business Master an extension exists
 * (AbstractConfiguredBMJob) which provides the necessary JobConfigSource) TODO: Determine first run and non first run
 * of job and pass respective limit/config to execution method
 *
 * @author  Thomas Zirke
 */
public abstract class AbstractConfiguredJob extends AbstractJob implements Job {

    /**
     * Logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(AbstractConfiguredJob.class);

    /**
     * Default Constructor.
     */
    protected AbstractConfiguredJob() {
        super();
    }

    @Override
    protected void registerListener() {
        super.registerListener();
        addJobListener(getApplicationContext().getBean(JobFlowIdListener.beanName(), JobFlowIdListener.class));
        addJobListener(getApplicationContext().getBean(JobHistoryListener.beanName(), JobHistoryListener.class));
    }

    /**
     * Do Run Implementation - calls new doRun method also containing a {@link JobConfig} instance for the current job.
     * This method is not meant to be overridden - for extensions of this class implement the new doRun method provided
     * by this class
     *
     * @throws  Exception
     */
    @Override
    protected final void doRun(final JobExecutionContext context) throws Exception {
        final long startTime = System.currentTimeMillis();

        // Put execution Count for Job into static Counter Map if it does not
        // exist
        // This can be used to determine if it is the Jobs first run in current
        // JVM
        try {

            // Fetch and validate JobConfig
            if (LOG.isDebugEnabled()) {
                LOG.debug("Fetching JobConfig from ApplicationConfiguration for Job: " + this.getBeanName());
            }

            final JobConfig config = getConfigurationSource().getJobConfig(this);
            if (config == null) {

                // Maybe an IllegalArgumentException should be thrown here?
                LOG.fatal(String.format(
                        "JobConfig for ComponentBean (Job): %s could not be retrieved from Application Config.",
                        this.getBeanName()));
                return;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Job: %s has JobConfig: %s", this.getBeanName(), config.toString()));
            }

            // Check that the current Job is globally active
            if (!config.isActive()) {
                LOG.info(String.format(
                        "Job: %1$s has been deactivated. To activate the Job set the "
                            + "Configuration Property: 'jobConfig.%1$s.active' to 'true'", this.getBeanName()));
                return;
            }

            // Check if the JobGroup is deactivated
            if ((config.getJobGroupConfig() != null) && !config.getJobGroupConfig().isJobGroupActive()) {
                LOG.info(String.format(
                        "Job: '%1$s' is part of the JobGroup: '%2$s' which is deactived. "
                            + "Skipping Job Execution...", this.getBeanName(),
                        config.getJobGroupConfig().getJobGroupName()));

                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format(
                            "To activate the JobGroup: '%1$s' set the Configuration Property: "
                                + "'jobGroupConfig.%1$s.active' to 'true'. "
                                + "ATTENTION: Please note that (de)activation of a JobGroup will most likely affect "
                                + "all Jobs in that JobGroup. Be sure you want to (de)activate all Jobs in "
                                + "that particular JobGroup.", config.getJobGroupConfig().getJobGroupName()));
                }

                return;
            }

            // Check that the current Job can be executed on current Machine
            if (!config.isAllowedAppInstanceKey(getConfigurationSource().getAppInstanceKey())) {
                LOG.info(String.format(
                        "Job: %1$s cannot run on current Instance: %2$s. Allowed Instances: %3$s"
                            + ". The Configuration Property: 'jobConfig.%1$s.appInstanceKey' "
                            + "contains a comma separated list of Application "
                            + "Instances allowed to execute the Job.", this.getBeanName(),
                        getConfigurationSource().getAppInstanceKey(), config.getAllowedAppInstanceKeys()));
                return;
            }

            // Execute the Job
            LOG.info(String.format("Starting Job: %1$s", this.getBeanName()));

            // Exceptions may occur here - will be thrown to calling component
            // (AbstractJob)
            doRun(context, config);
            LOG.info(String.format("Finished Job: %1$s", this.getBeanName()));
        } catch (final Exception e) {
            LOG.warn(String.format("Job: %s failed with Exception: %s. Error Message was: %s", getBeanName(),
                    e.getClass().getSimpleName(), e.getMessage()));
            throw e;
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Executing Job: %1$s took: %2$d ms", this.getBeanName(),
                        (System.currentTimeMillis() - startTime)));
            }
        }
    }
}
