package de.zalando.zomcat.jobs.management;

import static junit.framework.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.JobGroupConfig;

public class JobSchedulingConfigurationTest {

    private static final transient String APP_INSTANCE_KEY1 = "app_instance_1";

    private static final transient String APP_INSTANCE_KEY2 = "app_instance_2";

    private static final transient String JOB_GROUP_NAME = "some job group";

    private static final transient String ALL_APP_INSTANCES_KEY = "*";

    private JobSchedulingConfiguration prepareCronJobSchedulingConfiguration(final String cronExpression,
            final JobConfig jobConfig) {

        final Map<String, String> jobData = Maps.newHashMap();
        jobData.put("SomeKey", "SomeValue");

        return new JobSchedulingConfiguration(cronExpression, "Some Job Class", jobData, jobConfig);

    }

    private JobSchedulingConfiguration prepareSimpleJobSchedulingConfiguration(final int delayMS, final int intervalMS,
            final JobConfig jobConfig) {
        final Set<String> jobAppInstanceKeys = Sets.newHashSet();
        jobAppInstanceKeys.add(APP_INSTANCE_KEY1);

        final Set<String> jobGroupAppInstanceKeys = Sets.newHashSet();

        final Map<String, String> jobData = Maps.newHashMap();
        jobData.put("SomeKey", "SomeValue");

        return new JobSchedulingConfiguration(delayMS, intervalMS, "Some Job Class", jobData, jobConfig);

    }

    private JobConfig prepareJobConfig(final String jobName, final boolean active, final int startupLimit,
            final int limit, final JobGroupConfig jobGroupConfig) {
        final Set<String> jobAppInstanceKeys = Sets.newHashSet();
        jobAppInstanceKeys.add(APP_INSTANCE_KEY1);

        return new JobConfig(jobAppInstanceKeys, 50, 1000, true, jobGroupConfig);

    }

    private JobGroupConfig prepareJobGroupConfig(final String jobGroupName, final boolean active) {
        final Set<String> jobGroupAppInstanceKeys = Sets.newHashSet();
        return new JobGroupConfig(jobGroupName, active, jobGroupAppInstanceKeys);
    }

    @Test
    public void testIsAlteredOnCronExpressionChange() {
        final JobSchedulingConfiguration jsc1 = prepareCronJobSchedulingConfiguration("0 1 2 * * ?",
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));
        final JobSchedulingConfiguration jsc2 = prepareCronJobSchedulingConfiguration("0 1 3 * * ?",
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));

        assertTrue(!jsc1.isEqual(jsc2));

        final JobSchedulingConfiguration jsc3 = prepareCronJobSchedulingConfiguration("0 1 2 * * ?",
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));
        final JobSchedulingConfiguration jsc4 = prepareCronJobSchedulingConfiguration("0 1 2 * * ?",
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));

        assertTrue(jsc3.isEqual(jsc4));
    }

    @Test
    public void testIsAlteredOnIntervalChange() {
        final JobSchedulingConfiguration jsc1 = prepareSimpleJobSchedulingConfiguration(10, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));
        final JobSchedulingConfiguration jsc2 = prepareSimpleJobSchedulingConfiguration(10, 1000,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));

        assertTrue(!jsc1.isEqual(jsc2));

        final JobSchedulingConfiguration jsc3 = prepareSimpleJobSchedulingConfiguration(10, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));
        final JobSchedulingConfiguration jsc4 = prepareSimpleJobSchedulingConfiguration(10, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));

        assertTrue(jsc3.isEqual(jsc4));
    }

    @Test
    public void testIsAlteredOnDelayChange() {
        final JobSchedulingConfiguration jsc1 = prepareSimpleJobSchedulingConfiguration(10, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));
        final JobSchedulingConfiguration jsc2 = prepareSimpleJobSchedulingConfiguration(100, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));

        assertTrue(!jsc1.isEqual(jsc2));

        final JobSchedulingConfiguration jsc3 = prepareSimpleJobSchedulingConfiguration(10, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));
        final JobSchedulingConfiguration jsc4 = prepareSimpleJobSchedulingConfiguration(10, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));

        assertTrue(jsc3.isEqual(jsc4));
    }

    @Test
    public void testIsAlteredOnJobConfigAppInstanceKeysChange() {
        final JobSchedulingConfiguration jsc1 = prepareSimpleJobSchedulingConfiguration(10, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));
        final Set<String> jobAppInstanceKeys = Sets.newHashSet();
        jobAppInstanceKeys.add(APP_INSTANCE_KEY2);

        final JobConfig jc2 = new JobConfig(jobAppInstanceKeys, 50, 100, true,
                prepareJobGroupConfig(JOB_GROUP_NAME, true));
        final JobSchedulingConfiguration jsc2 = prepareSimpleJobSchedulingConfiguration(100, 100, jc2);

        assertTrue(!jsc1.isEqual(jsc2));

        final JobSchedulingConfiguration jsc3 = prepareSimpleJobSchedulingConfiguration(10, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));
        final JobSchedulingConfiguration jsc4 = prepareSimpleJobSchedulingConfiguration(10, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));

        assertTrue(jsc3.isEqual(jsc4));

    }

    @Test
    public void testIsAlteredOnJobConfigActiveStateChange() {
        final JobSchedulingConfiguration jsc1 = prepareSimpleJobSchedulingConfiguration(10, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));
        final JobSchedulingConfiguration jsc2 = prepareSimpleJobSchedulingConfiguration(100, 100,
                prepareJobConfig("jobName", false, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));

        assertTrue(!jsc1.isEqual(jsc2));

        final JobSchedulingConfiguration jsc3 = prepareSimpleJobSchedulingConfiguration(10, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));
        final JobSchedulingConfiguration jsc4 = prepareSimpleJobSchedulingConfiguration(10, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));

        assertTrue(jsc3.isEqual(jsc4));
    }

    @Test
    public void testIsAlteredOnJobGroupConfigActiveStateChange() {
        final JobSchedulingConfiguration jsc1 = prepareSimpleJobSchedulingConfiguration(10, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));
        final JobSchedulingConfiguration jsc2 = prepareSimpleJobSchedulingConfiguration(100, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, false)));

        assertTrue(!jsc1.isEqual(jsc2));

        final JobSchedulingConfiguration jsc3 = prepareSimpleJobSchedulingConfiguration(10, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));
        final JobSchedulingConfiguration jsc4 = prepareSimpleJobSchedulingConfiguration(10, 100,
                prepareJobConfig("jobName", true, 50, 100, prepareJobGroupConfig(JOB_GROUP_NAME, true)));

        assertTrue(jsc3.isEqual(jsc4));
    }

}
