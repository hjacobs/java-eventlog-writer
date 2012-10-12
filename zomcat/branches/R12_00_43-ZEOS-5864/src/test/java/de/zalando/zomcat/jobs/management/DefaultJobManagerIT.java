package de.zalando.zomcat.jobs.management;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.JobGroupConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:jobManagerBackendContextTest.xml"})
public class DefaultJobManagerIT {

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

    // Test Creation and Destruction of Jobmanager - with Managed Jobs
    // Also Test seperate Getter Methods for Scheduled and Unscheduled Jobs
    @Test
    public void testStartupAndShutdown() throws JobManagerException {
        final Map<String, String> jobData = Maps.newHashMap();
        jobData.put("someKey", "someValue");

        final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                Sets.newHashSet("local_local"), true, null));

        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false, null));

        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                Sets.newHashSet("local_local"), true, null));

        ((TestJobSchedulingConfigProvider) configProvider).setConfigurationsToProvide(jobSchedulingConfigurations);
        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true, null));

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

    }

    // Test Update of Scheduling Configuration
    @Test
    public void testUpdateSchedulingConfiguration() throws JobManagerException {
        final Map<String, String> jobData = Maps.newHashMap();
        jobData.put("someKey", "someValue");

        final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                Sets.newHashSet("local_local"), true, null));

        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false, null));

        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                Sets.newHashSet("local_local"), true, null));

        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true, null));
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

    }

    // Test Override Job Active State <code>true</code> and <code>false</code>
    @Test
    public void testToggleJob() throws JobManagerException {
        final Map<String, String> jobData = Maps.newHashMap();
        jobData.put("someKey", "someValue");

        final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                Sets.newHashSet("local_local"), true, null));

        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false, null));

        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                Sets.newHashSet("local_local"), true, null));

        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true, null));
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

    }

    // Test Override Job Group Active State <code>true</code> and <code>false</code>
    // Test Override Job Active State <code>true</code> and <code>false</code>
    @Test
    public void testToggleJobGroup() throws JobManagerException {
        final Map<String, String> jobData = Maps.newHashMap();
        jobData.put("someKey", "someValue");

        final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                Sets.newHashSet("local_local"), true, null));

        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false, null));

        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                Sets.newHashSet("local_local"), true, null));

        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true, null));
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

    }

    // Test altered JobSchedulingConfiguration provided by Provider
    @Test
    public void testAlteredJobSchedulingConfigFromProvider() throws JobManagerException {
        final Map<String, String> jobData = Maps.newHashMap();
        jobData.put("someKey", "someValue");

        final List<JobSchedulingConfiguration> jobSchedulingConfigurations = Lists.newArrayList();
        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob1Job", new HashMap<String, String>(),
                Sets.newHashSet("local_local"), true, null));

        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 0 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob1Job", jobData, Sets.newHashSet("local_local"), false, null));

        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob2Job", new HashMap<String, String>(),
                Sets.newHashSet("local_local"), true, null));

        jobSchedulingConfigurations.add(createJobSchedulingConfiguration("0 0 1 * * ?",
                "de.zalando.zomcat.jobs.management.TestJob2Job", jobData, Sets.newHashSet("local_local"), true, null));
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

    }

    // Test wait for Jobs to complete on shutdown
}
