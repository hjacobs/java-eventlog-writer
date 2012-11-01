package de.zalando.zomcat.jobs.management;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.zalando.zomcat.jobs.AbstractJob;
import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.JobGroupConfig;

/**
 * Abstract Testcases for {@link JobManager} implementations. Two extensions to this BaseClass exist performing the same
 * tests for different JobManager implementations. Triggering an unscheduled Job is currently not possible. The
 * {@link AbstractJob} class contains the isRunnable logic that accertains if the respectively current Job may be
 * executed in current environment. This logic is performed by the JobManager. Nevertheless even unscheduled Jobs should
 * be triggerable.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
@RunWith(SpringJUnit4ClassRunner.class)
public abstract class AbstractJobManagerIT {

    @Autowired
    private JobSchedulingConfigurationProvider configProvider;

    @Autowired
    private JobManager jobManagerToTest;

    /**
     * Create a single {@link JobSchedulingConfiguration} instance for given Parameters.
     *
     * @param   cronExpression   The CRON Expression
     * @param   jobClass         The Jobs Fully Qualified Classname
     * @param   jobData          The JobData Parameter Map
     * @param   appInstanceKeys  Allowed AppInstanceKeys to use
     * @param   active           The active State of Job
     * @param   jobGroupName     The JobGroup Name - <code>null</code> for default group
     *
     * @return  The {@link JobSchedulingConfiguration} instance created from given Data
     */
    private JobSchedulingConfiguration createJobSchedulingConfiguration(final String cronExpression,
            final String jobClass, final Map<String, String> jobData, final Set<String> appInstanceKeys,
            final boolean active, final String jobGroupName) {
        JobGroupConfig jobGroupConfig = null;
        if (jobGroupName != null) {
            jobGroupConfig = new JobGroupConfig(jobGroupName, true, null);
        }

        final JobConfig jobConfig = new JobConfig(appInstanceKeys, 50, 1000, active, jobGroupConfig);
        final JobSchedulingConfiguration retVal = new JobSchedulingConfiguration(cronExpression, jobClass,
                "Some Job Description", jobData, jobConfig);
        return retVal;
    }

    /**
     * Test Startup and Shutdown of JobManager.
     *
     * @throws  JobManagerException  if any error occurs during Startup or Shutdown
     */
    @Test
    public void testStartupAndShutdown() throws JobManagerException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 4);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 3);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);

            jobManagerToTest.shutdown();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertTrue(jobManagerToTest.getManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test behavior of JobManager updating Scheduling Configurations if a Jobs class cannot be found or loaded.
     *
     * @throws  JobManagerException  if any error occurs during Startup or Shutdown
     */
    @Test
    public void testJobJavaClassNotFound() throws JobManagerException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob12Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 3);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 2);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);

            jobManagerToTest.shutdown();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertTrue(jobManagerToTest.getManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test triggering Job.
     *
     * @throws  JobManagerException   - If an exception occurs during scheduling
     * @throws  InterruptedException  - If waiting for Job to be performed throws an error
     */
    @Test
    public void testTriggerJob() throws JobManagerException, InterruptedException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            // ((TestJobSchedulingConfigProvider)
            // configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));

            final JobSchedulingConfiguration jscToWaitFor = createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJobToWaitForJob", jobData, Sets.newHashSet("local_local"),
                    true, null);

            jobSchedulingConfigurations.add(jscToWaitFor);

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 5);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 4);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);

            jobManagerToTest.triggerJob(jscToWaitFor, true);

            // Wait for Job to have been started
            Thread.sleep(1000);
            assertNotNull(jobManagerToTest.getManagedJob(jscToWaitFor));
            assertEquals(1, jobManagerToTest.getManagedJob(jscToWaitFor).getRunningWorkerCount());

            final long curTime = System.currentTimeMillis();
            jobManagerToTest.shutdown();

            final long finishedShutdownTime = System.currentTimeMillis() - curTime;
            assertTrue(finishedShutdownTime > 4000);
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertTrue(jobManagerToTest.getManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test triggering Job multiple Times breaching the max concurrent thread count. Excess scheduled Executions of the
     * Job must be prevented when the Max amount of Executions is reached at trigger time of Job.
     *
     * @throws  JobManagerException   - If an exception occurs during scheduling
     * @throws  InterruptedException  - If waiting for Job to be performed throws an error
     */
    @Test
    public void testTriggerJobWithMaxConcurrentLimitBreach() throws JobManagerException, ClassNotFoundException,
        InterruptedException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            // ((TestJobSchedulingConfigProvider)
            // configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));

            final JobSchedulingConfiguration jscToWaitFor = createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJobToWaitForJob", jobData, Sets.newHashSet("local_local"),
                    true, null);

            jobSchedulingConfigurations.add(jscToWaitFor);

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 5);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 4);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);

            jobManagerToTest.triggerJob(jscToWaitFor, true);
            jobManagerToTest.triggerJob(jscToWaitFor, true);
            jobManagerToTest.triggerJob(jscToWaitFor, true);
            jobManagerToTest.triggerJob(jscToWaitFor, true);

            // Wait for Job to have been started
            Thread.sleep(1000);
            assertNotNull(jobManagerToTest.getManagedJob(jscToWaitFor));
            assertEquals(1, jobManagerToTest.getManagedJob(jscToWaitFor).getRunningWorkerCount());

            final long curTime = System.currentTimeMillis();
            jobManagerToTest.shutdown();

            final long finishedShutdownTime = System.currentTimeMillis() - curTime;
            assertTrue(finishedShutdownTime > 4000);
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertTrue(jobManagerToTest.getManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test triggering an unscheduled Job. This testcase will fail until the isRunnable logic has been removed from
     * {@link AbstractJob} class. The isRunnable logic is now performed by the JobManager. Removal of the isRunnable
     * logic from AbstractJob class will occur once the old style Job Management solution is fully deprectated and no
     * longer used.
     *
     * @throws  JobManagerException   - If an exception occurs during scheduling
     * @throws  InterruptedException  - If waiting for Job to be performed throws an error
     */
    @Test
    public void testTriggerUnscheduledJob() throws JobManagerException, InterruptedException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            final JobSchedulingConfiguration jscToNotSchedule = createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJobToWaitForJob", jobData, Sets.newHashSet("local_local"),
                    false, null);
            jobSchedulingConfigurations.add(jscToNotSchedule);

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            // ((TestJobSchedulingConfigProvider)
            // configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 4);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 3);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);

            jobManagerToTest.triggerJob(jscToNotSchedule, true);

            // Wait for Job to have been started
            Thread.sleep(1000);
            assertNotNull(jobManagerToTest.getManagedJob(jscToNotSchedule));
            assertEquals(1, jobManagerToTest.getManagedJob(jscToNotSchedule).getRunningWorkerCount());

            final long curTime = System.currentTimeMillis();
            jobManagerToTest.shutdown();

            final long finishedShutdownTime = System.currentTimeMillis() - curTime;
            assertTrue(finishedShutdownTime > 4000);
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertTrue(jobManagerToTest.getManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test triggering an unscheduled Job. This testcase will fail until the isRunnable logic has been removed from
     * {@link AbstractJob} class. The isRunnable logic is now performed by the JobManager. Removal of the isRunnable
     * logic from AbstractJob class will occur once the old style Job Management solution is fully deprectated and no
     * longer used.
     *
     * @throws  JobManagerException   - If an exception occurs during scheduling
     * @throws  InterruptedException  - If waiting for Job to be performed throws an error
     */
    @Test
    public void testTriggerUnscheduledJobWithMaxConcurrentLimitBreach() throws JobManagerException,
        InterruptedException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            final JobSchedulingConfiguration jscToNotSchedule = createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJobToWaitForJob", jobData, Sets.newHashSet("local_local"),
                    false, null);
            jobSchedulingConfigurations.add(jscToNotSchedule);

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            // ((TestJobSchedulingConfigProvider)
            // configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 4);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 3);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);

            jobManagerToTest.triggerJob(jscToNotSchedule, true);
            jobManagerToTest.triggerJob(jscToNotSchedule, true);
            jobManagerToTest.triggerJob(jscToNotSchedule, true);
            jobManagerToTest.triggerJob(jscToNotSchedule, true);

            // Wait for Job to have been started
            Thread.sleep(1000);
            assertNotNull(jobManagerToTest.getManagedJob(jscToNotSchedule));
            assertEquals(1, jobManagerToTest.getManagedJob(jscToNotSchedule).getRunningWorkerCount());

            final long curTime = System.currentTimeMillis();
            jobManagerToTest.shutdown();

            final long finishedShutdownTime = System.currentTimeMillis() - curTime;
            assertTrue(finishedShutdownTime > 4000);
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertTrue(jobManagerToTest.getManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test Startup and Shutdown of JobManager while Jobs are still running. Validate that the JobManager shutdown will
     * halt until all Jobs have finished their work.
     *
     * @throws  JobManagerException   if an unanticipated error occurs during startup or shutdown
     * @throws  InterruptedException  if Thread.sleep fails
     */
    @Test
    public void testStartupAndShutdownWithWaitForJobToComplete() throws JobManagerException, InterruptedException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            // ((TestJobSchedulingConfigProvider)
            // configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));

            final JobSchedulingConfiguration jscToWaitFor = createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJobToWaitForJob", jobData, Sets.newHashSet("local_local"),
                    true, null);

            jobSchedulingConfigurations.add(jscToWaitFor);

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 5);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 4);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);

            jobManagerToTest.triggerJob(jscToWaitFor, true);
            Thread.sleep(100);

            final long curTime = System.currentTimeMillis();
            jobManagerToTest.shutdown();

            final long finishedShutdownTime = System.currentTimeMillis() - curTime;
            assertTrue(finishedShutdownTime > 4000);
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertTrue(jobManagerToTest.getManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test update of JobSchedulingConfiguration.
     *
     * @throws  JobManagerException  if any unanticipated error occurs (fatal exception may only occur if the
     *                               {@link JobSchedulingConfigurationProvider} throws an exception)
     */
    @Test
    public void testUpdateSchedulingConfiguration() throws JobManagerException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));
            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(null);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertTrue(jobManagerToTest.getManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobManagerToTest.updateJobSchedulingConfigurations();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 4);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 3);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(null);
            jobManagerToTest.updateJobSchedulingConfigurations();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertTrue(jobManagerToTest.getManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            jobManagerToTest.shutdown();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertTrue(jobManagerToTest.getManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test toggle of Job. Validate that toggling a Job yields the opposite active state of Job as has been previously
     * set.
     *
     * @throws  JobManagerException  - if any error occurs toggling the Job
     */
    @Test
    public void testToggleJob() throws JobManagerException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));
            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(4, jobManagerToTest.getManagedJobs().size());
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(3, jobManagerToTest.getScheduledManagedJobs().size());
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(1, jobManagerToTest.getUnscheduledManagedJobs().size());

            jobManagerToTest.toggleJob(jobSchedulingConfigurations.get(1),
                !jobSchedulingConfigurations.get(1).getJobConfig().isActive());
            jobManagerToTest.toggleJob(jobSchedulingConfigurations.get(2),
                !jobSchedulingConfigurations.get(2).getJobConfig().isActive());
            jobManagerToTest.toggleJob(jobSchedulingConfigurations.get(3),
                !jobSchedulingConfigurations.get(3).getJobConfig().isActive());

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(4, jobManagerToTest.getManagedJobs().size());
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(2, jobManagerToTest.getScheduledManagedJobs().size());
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(2, jobManagerToTest.getUnscheduledManagedJobs().size());

            jobManagerToTest.toggleJob(jobSchedulingConfigurations.get(1),
                jobSchedulingConfigurations.get(1).getJobConfig().isActive());
            jobManagerToTest.toggleJob(jobSchedulingConfigurations.get(2),
                jobSchedulingConfigurations.get(2).getJobConfig().isActive());
            jobManagerToTest.toggleJob(jobSchedulingConfigurations.get(3),
                jobSchedulingConfigurations.get(3).getJobConfig().isActive());

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(4, jobManagerToTest.getManagedJobs().size());
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(3, jobManagerToTest.getScheduledManagedJobs().size());
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(1, jobManagerToTest.getUnscheduledManagedJobs().size());
            jobManagerToTest.shutdown();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertTrue(jobManagerToTest.getManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test toggle of Job. Validate that toggling a Job yields the opposite active state of Job as has been previously
     * set.
     *
     * @throws  JobManagerException  - if any error occurs toggling the Job
     */
    @Test
    public void testToggleJobInvalidJobIdentifier() throws JobManagerException {
        final Map<String, String> jobData = Maps.newHashMap();
        jobData.put("someKey", "someValue");

        final JobSchedulingConfiguration jobToToggle = createJobSchedulingConfiguration("0 0 0 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob12Job", jobData, Sets.newHashSet("local_local"), false, null);
        try {

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));
            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(4, jobManagerToTest.getManagedJobs().size());
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(3, jobManagerToTest.getScheduledManagedJobs().size());
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(1, jobManagerToTest.getUnscheduledManagedJobs().size());

            jobManagerToTest.toggleJob(jobSchedulingConfigurations.get(1),
                !jobSchedulingConfigurations.get(1).getJobConfig().isActive());
            jobManagerToTest.toggleJob(jobSchedulingConfigurations.get(2),
                !jobSchedulingConfigurations.get(2).getJobConfig().isActive());
            jobManagerToTest.toggleJob(jobSchedulingConfigurations.get(3),
                !jobSchedulingConfigurations.get(3).getJobConfig().isActive());

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(4, jobManagerToTest.getManagedJobs().size());
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(2, jobManagerToTest.getScheduledManagedJobs().size());
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(2, jobManagerToTest.getUnscheduledManagedJobs().size());

            jobManagerToTest.toggleJob(jobToToggle, !jobToToggle.getJobConfig().isActive());
        } catch (final JobManagerException e) {
            assertEquals(String.format("Could not find Managed Job for JobSchedulingConfiguration: [%s]", jobToToggle),
                e.getMessage());

        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Toggle an entire JobGroup. Validate that all Jobs in Group are being unscheduled and rescheduled on additional
     * toggle.
     *
     * @throws  JobManagerException  if any error occurs toggling the test job group
     */
    @Test
    public void testToggleJobGroup() throws JobManagerException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));
            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(4, jobManagerToTest.getManagedJobs().size());
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(3, jobManagerToTest.getScheduledManagedJobs().size());
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(1, jobManagerToTest.getUnscheduledManagedJobs().size());

            jobManagerToTest.toggleJobGroup("none");

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(4, jobManagerToTest.getManagedJobs().size());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(0, jobManagerToTest.getScheduledManagedJobs().size());
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(4, jobManagerToTest.getUnscheduledManagedJobs().size());

            jobManagerToTest.toggleJobGroup("none");

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(4, jobManagerToTest.getManagedJobs().size());
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(3, jobManagerToTest.getScheduledManagedJobs().size());
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(1, jobManagerToTest.getUnscheduledManagedJobs().size());
            jobManagerToTest.shutdown();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertTrue(jobManagerToTest.getManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test altered {@link JobSchedulingConfiguration}s being provided by {@link JobSchedulingConfigurationProvider}.
     *
     * @throws  JobManagerException  if any error occurs updateing the {@link JobSchedulingConfiguration} provided
     */
    @Test
    public void testAlteredJobSchedulingConfigFromProvider() throws JobManagerException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));
            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(4, jobManagerToTest.getManagedJobs().size());
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(3, jobManagerToTest.getScheduledManagedJobs().size());
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(1, jobManagerToTest.getUnscheduledManagedJobs().size());

            jobSchedulingConfigurations.remove(0);
            jobSchedulingConfigurations.add(0,
                createJobSchedulingConfiguration("0 0 0 * * ?", "de.zalando.zomcat.jobs.management.TestJob1Job",
                    new HashMap<String, String>(), Sets.newHashSet("some_app_instance"), true, null));
            jobManagerToTest.updateJobSchedulingConfigurations();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(4, jobManagerToTest.getManagedJobs().size());
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(2, jobManagerToTest.getScheduledManagedJobs().size());
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(2, jobManagerToTest.getUnscheduledManagedJobs().size());

            jobSchedulingConfigurations.remove(0);
            jobSchedulingConfigurations.add(0,
                createJobSchedulingConfiguration("0 0 0 * * ?", "de.zalando.zomcat.jobs.management.TestJob1Job",
                    new HashMap<String, String>(), Sets.newHashSet("*"), true, null));
            jobManagerToTest.updateJobSchedulingConfigurations();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(4, jobManagerToTest.getManagedJobs().size());
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(3, jobManagerToTest.getScheduledManagedJobs().size());
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(1, jobManagerToTest.getUnscheduledManagedJobs().size());

            jobSchedulingConfigurations.remove(0);
            jobSchedulingConfigurations.add(0,
                createJobSchedulingConfiguration("0 0 0 * * ?", "de.zalando.zomcat.jobs.management.TestJob1Job",
                    new HashMap<String, String>(), Sets.newHashSet("local_local"), true, null));
            jobManagerToTest.updateJobSchedulingConfigurations();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(4, jobManagerToTest.getManagedJobs().size());
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(3, jobManagerToTest.getScheduledManagedJobs().size());
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(1, jobManagerToTest.getUnscheduledManagedJobs().size());

            jobManagerToTest.shutdown();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertTrue(jobManagerToTest.getManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test setting of operation mode on JobManger. Setting JobManager to MAINTENANCE Mode will cancel all active Jobs.
     * It will not wait however for all running workers to have finished. Setting operation mode to NORMAL will
     * reschedule all jobs supposed to be running on Instance.
     *
     * @throws  JobManagerException  if any error occurs setting the JobManagers operation mode.
     */
    @Test
    public void testSetOperationMode() throws JobManagerException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            // ((TestJobSchedulingConfigProvider)
            // configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));

            final JobSchedulingConfiguration jscToWaitFor = createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJobToWaitForJob", jobData, Sets.newHashSet("local_local"),
                    true, null);

            jobSchedulingConfigurations.add(jscToWaitFor);

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(5, jobManagerToTest.getManagedJobs().size());
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(4, jobManagerToTest.getScheduledManagedJobs().size());
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(1, jobManagerToTest.getUnscheduledManagedJobs().size());

            jobManagerToTest.setMainanenceModeActive(true);
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(5, jobManagerToTest.getManagedJobs().size());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(0, jobManagerToTest.getScheduledManagedJobs().size());
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(5, jobManagerToTest.getUnscheduledManagedJobs().size());
            assertTrue(jobManagerToTest.isMainanenceModeActive());
            jobManagerToTest.setMainanenceModeActive(false);

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 5);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 4);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);
            assertFalse(jobManagerToTest.isMainanenceModeActive());

            jobManagerToTest.shutdown();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertTrue(jobManagerToTest.getManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test setting the OperationMode to MAINTENANCE while Jobs are running and shutting down the JobManager afterwards.
     * Validate that even in MAINTENANCE Mode the JobManger waits for all RunningWorkers to complete before completing
     * its own shutdown.
     *
     * @throws  JobManagerException  if any error occurs setting the OperationMode or shutting down the JobManager.
     */
    @Test
    public void testSetOperationModeThenShutdown() throws JobManagerException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            // ((TestJobSchedulingConfigProvider)
            // configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));

            final JobSchedulingConfiguration jscToWaitFor = createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJobToWaitForJob", jobData, Sets.newHashSet("local_local"),
                    true, null);

            jobSchedulingConfigurations.add(jscToWaitFor);

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 5);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 4);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);

            jobManagerToTest.setMainanenceModeActive(true);
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 5);
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 0);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 5);

            jobManagerToTest.shutdown();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertTrue(jobManagerToTest.getManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test getting a Managed Job from {@link JobManager} asking for NULL {@link JobSchedulingConfiguration}. An
     * Exception is expected to be thrown.
     *
     * @throws  JobManagerException  if any error occurs setting the OperationMode or shutting down the JobManager.
     */
    @Test
    public void testGetManagedJobNullJobSchedulingConfiguration() throws JobManagerException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            // ((TestJobSchedulingConfigProvider)
            // configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));

            final JobSchedulingConfiguration jscToWaitFor = createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJobToWaitForJob", jobData, Sets.newHashSet("local_local"),
                    true, null);

            jobSchedulingConfigurations.add(jscToWaitFor);

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 5);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 4);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);

            jobManagerToTest.getManagedJob(null);
        } catch (final JobManagerException e) {
            assertEquals("JobSchedulingConfiguration to find Job by cannot be null", e.getMessage());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test getting a Managed Job from {@link JobManager} asking for NULL JobName. An Exception is expected to be
     * thrown.
     *
     * @throws  JobManagerException  if any error occurs setting the OperationMode or shutting down the JobManager.
     */
    @Test
    public void testGetManagedJobNullQuartzJobDetailName() throws JobManagerException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            // ((TestJobSchedulingConfigProvider)
            // configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));

            final JobSchedulingConfiguration jscToWaitFor = createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJobToWaitForJob", jobData, Sets.newHashSet("local_local"),
                    true, null);

            jobSchedulingConfigurations.add(jscToWaitFor);

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 5);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 4);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);

            jobManagerToTest.getManagedJob(null, "none");
        } catch (final JobManagerException e) {
            assertEquals("Quartz JobDetail Name to find Job for cannot be null", e.getMessage());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test getting a Managed Job from {@link JobManager} asking for NULL JobGroup. An Exception is expected to be
     * thrown.
     *
     * @throws  JobManagerException  if any error occurs setting the OperationMode or shutting down the JobManager.
     */
    @Test
    public void testGetManagedJobNullQuartzJobDetailGroup() throws JobManagerException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            // ((TestJobSchedulingConfigProvider)
            // configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));

            final JobSchedulingConfiguration jscToWaitFor = createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJobToWaitForJob", jobData, Sets.newHashSet("local_local"),
                    true, null);

            jobSchedulingConfigurations.add(jscToWaitFor);

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 5);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 4);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);

            jobManagerToTest.getManagedJob("someJobId", null);
        } catch (final JobManagerException e) {
            assertEquals("Quartz JobDetail Group to find Job for cannot be null", e.getMessage());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test getting a Managed Job from {@link JobManager} asking for NULL JobGroup. NULL {@link JobManagerManagedJob} is
     * expected to be returned
     *
     * @throws  JobManagerException  if any error occurs setting the OperationMode or shutting down the JobManager.
     */
    @Test
    public void testGetManagedJobInvalidQuartzJobDetailName() throws JobManagerException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            // ((TestJobSchedulingConfigProvider)
            // configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));

            final JobSchedulingConfiguration jscToWaitFor = createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJobToWaitForJob", jobData, Sets.newHashSet("local_local"),
                    true, null);

            jobSchedulingConfigurations.add(jscToWaitFor);

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 5);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 4);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);

            final JobManagerManagedJob managedJob = jobManagerToTest.getManagedJob("someJobId", "none");
            assertNull(managedJob);
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test getting a Managed Job from {@link JobManager} asking for NULL JobGroup. NULL {@link JobManagerManagedJob} is
     * expected to be returned
     *
     * @throws  JobManagerException  if any error occurs setting the OperationMode or shutting down the JobManager.
     */
    @Test
    public void testGetManagedJobInvalidQuartzJobDetailGroup() throws JobManagerException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            // ((TestJobSchedulingConfigProvider)
            // configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));

            final JobSchedulingConfiguration jscToWaitFor = createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJobToWaitForJob", jobData, Sets.newHashSet("local_local"),
                    true, null);

            jobSchedulingConfigurations.add(jscToWaitFor);

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 5);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 4);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);

            final JobManagerManagedJob managedJob = jobManagerToTest.getManagedJob("testJob1Job1", "some_job_group");
            assertNull(managedJob);
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test getting a Managed Job from {@link JobManager} asking for NULL JobGroup. NULL {@link JobManagerManagedJob} is
     * expected to be returned
     *
     * @throws  JobManagerException  if any error occurs setting the OperationMode or shutting down the JobManager.
     */
    @Test
    public void testGetManagedJobInvalidJobSchedulingConfiguration() throws JobManagerException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            // ((TestJobSchedulingConfigProvider)
            // configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));

            final JobSchedulingConfiguration jscToWaitFor = createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJobToWaitForJob", jobData, Sets.newHashSet("local_local"),
                    true, null);

            jobSchedulingConfigurations.add(jscToWaitFor);

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 5);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 4);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);

            final JobSchedulingConfiguration jscToGetJobFor = createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob12Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null);
            final JobManagerManagedJob managedJob = jobManagerToTest.getManagedJob(jscToGetJobFor);
            assertNull(managedJob);
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test getting a Managed Job from {@link JobManager} asking for NULL JobGroup. NULL {@link JobManagerManagedJob} is
     * expected to be returned
     *
     * @throws  JobManagerException  if any error occurs setting the OperationMode or shutting down the JobManager.
     */
    @Test
    public void testGetManagedJobValidJobSchedulingConfiguration() throws JobManagerException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false,
                    null));

            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null));

            // ((TestJobSchedulingConfigProvider)
            // configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
            jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true,
                    null));

            final JobSchedulingConfiguration jscToWaitFor = createJobSchedulingConfiguration("0 0 1 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJobToWaitForJob", jobData, Sets.newHashSet("local_local"),
                    true, null);

            jobSchedulingConfigurations.add(jscToWaitFor);

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getManagedJobs().size(), 5);
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getScheduledManagedJobs().size(), 4);
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(jobManagerToTest.getUnscheduledManagedJobs().size(), 1);

            final JobSchedulingConfiguration jscToGetJobFor = createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null);
            final JobManagerManagedJob managedJob = jobManagerToTest.getManagedJob(jscToGetJobFor);
            assertNotNull(managedJob);
            assertEquals(jobSchedulingConfigurations.get(0), managedJob.getJobSchedulingConfig());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }

    /**
     * Test getting a Managed Job from {@link JobManager} asking for NULL JobGroup. NULL {@link JobManagerManagedJob} is
     * expected to be returned
     *
     * @throws  JobManagerException   if any error occurs setting the OperationMode or shutting down the JobManager.
     * @throws  InterruptedException  if Thread cannot be put to sleep (Thread.sleep)
     */
    @Test
    public void testCancelJobWaitThenRescheduleJob() throws JobManagerException, InterruptedException {
        try {
            final Map<String, String> jobData = Maps.newHashMap();
            jobData.put("someKey", "someValue");

            JobSchedulingConfiguration jsc = createJobSchedulingConfiguration("* * * * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null);
            final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
            jobSchedulingConfigurations.add(jsc);

            ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);

            assertNotNull(jobManagerToTest);

            jobManagerToTest.startup();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(1, jobManagerToTest.getManagedJobs().size());
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(1, jobManagerToTest.getScheduledManagedJobs().size());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());

            jsc = createJobSchedulingConfiguration("* * * * * ?", "de.zalando.zomcat.jobs.management.TestJob1Job",
                    new HashMap<String, String>(), Sets.newHashSet("local_local"), false, null);
            jobSchedulingConfigurations.clear();
            jobSchedulingConfigurations.add(jsc);

            jobManagerToTest.updateJobSchedulingConfigurations();
            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(1, jobManagerToTest.getManagedJobs().size());
            assertTrue(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertFalse(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            assertEquals(1, jobManagerToTest.getUnscheduledManagedJobs().size());

            Thread.sleep(2000);
            assertEquals(1, jobManagerToTest.getManagedJob(jsc).getExecutionCount());
            Thread.sleep(2000);
            assertEquals(1, jobManagerToTest.getManagedJob(jsc).getExecutionCount());

            jsc = createJobSchedulingConfiguration("* * * * * ?", "de.zalando.zomcat.jobs.management.TestJob1Job",
                    new HashMap<String, String>(), Sets.newHashSet("local_local"), true, null);
            jobSchedulingConfigurations.clear();
            jobSchedulingConfigurations.add(jsc);
            jobManagerToTest.updateJobSchedulingConfigurations();

            assertNotNull(jobManagerToTest.getManagedJobs());
            assertNotNull(jobManagerToTest.getScheduledManagedJobs());
            assertNotNull(jobManagerToTest.getUnscheduledManagedJobs());
            assertFalse(jobManagerToTest.getManagedJobs().isEmpty());
            assertEquals(1, jobManagerToTest.getManagedJobs().size());
            assertFalse(jobManagerToTest.getScheduledManagedJobs().isEmpty());
            assertEquals(1, jobManagerToTest.getScheduledManagedJobs().size());
            assertTrue(jobManagerToTest.getUnscheduledManagedJobs().isEmpty());
            Thread.sleep(200);
            assertTrue(jobManagerToTest.getManagedJob(jsc).getExecutionCount() >= 2);

            final JobSchedulingConfiguration jscToGetJobFor = createJobSchedulingConfiguration("0 0 0 * * ?",
                    "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                    Sets.newHashSet("local_local"), true, null);
            final JobManagerManagedJob managedJob = jobManagerToTest.getManagedJob(jscToGetJobFor);
            assertNotNull(managedJob);
            assertEquals(jobSchedulingConfigurations.get(0), managedJob.getJobSchedulingConfig());
        } finally {
            try {
                jobManagerToTest.shutdown();
            } catch (final JobManagerException e) { }
        }
    }
}
