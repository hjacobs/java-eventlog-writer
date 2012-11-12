package de.zalando.zomcat.jobs.management;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Sets;

import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.JobGroupConfig;

/**
 * Unittest(s) for {@link JobSchedulingConfiguration} class. Testing equals and isEqual methods
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public class JobSchedulingConfigurationTest {

    @Test
    public void testEqualsByJobClass() {
        final JobConfig jobConfig = new JobConfig(Sets.newHashSet("*"), 1000, 50, true, null);
        final JobSchedulingConfiguration jsc1 = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", new HashMap<String, String>(), jobConfig);
        final JobSchedulingConfiguration jsc2 = new JobSchedulingConfiguration("* * * * * ?",
                TestJob2Job.class.getName(), "SomeJobDescription", new HashMap<String, String>(), jobConfig);
        final JobSchedulingConfiguration jsc3 = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", new HashMap<String, String>(), jobConfig);

        assertNotNull(jsc1);
        assertNotNull(jsc2);
        assertNotNull(jsc3);

        assertEquals(false, jsc1.equals(jsc2));
        assertEquals(true, jsc1.equals(jsc3));
        assertEquals(false, jsc2.equals(jsc1));
        assertEquals(false, jsc2.equals(jsc3));
        assertEquals(true, jsc3.equals(jsc1));
        assertEquals(false, jsc3.equals(jsc2));

    }

    @Test
    public void testEqualsByJobDataMap() {
        final Map<String, String> dataMap1 = new HashMap<String, String>();
        final Map<String, String> dataMap2 = new HashMap<String, String>();
        dataMap2.put("SomeKey", "SomeValue");

        final Map<String, String> dataMap3 = new HashMap<String, String>();
        final JobConfig jobConfig = new JobConfig(Sets.newHashSet("*"), 1000, 50, true, null);
        final JobSchedulingConfiguration jsc1 = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap1, jobConfig);
        final JobSchedulingConfiguration jsc2 = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap2, jobConfig);
        final JobSchedulingConfiguration jsc3 = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap3, jobConfig);

        assertNotNull(jsc1);
        assertNotNull(jsc2);
        assertNotNull(jsc3);

        assertEquals(false, jsc1.equals(jsc2));
        assertEquals(true, jsc1.equals(jsc3));
        assertEquals(false, jsc2.equals(jsc1));
        assertEquals(false, jsc2.equals(jsc3));
        assertEquals(true, jsc3.equals(jsc1));
        assertEquals(false, jsc3.equals(jsc2));

    }

    @Test
    public void testEqualsByOtherDataChanged() {
        final Map<String, String> dataMap1 = new HashMap<String, String>();
        final Map<String, String> dataMap2 = new HashMap<String, String>();
        dataMap2.put("SomeKey", "SomeValue");

        final Map<String, String> dataMap3 = new HashMap<String, String>();
        final JobConfig jobConfig1 = new JobConfig(Sets.newHashSet("*"), 10, 50, true, null);
        final JobConfig jobConfig2 = new JobConfig(Sets.newHashSet("*"), 100, 50, true, null);
        final JobConfig jobConfig3 = new JobConfig(Sets.newHashSet("*"), 1000, 50, false, null);
        final JobSchedulingConfiguration jsc1 = new JobSchedulingConfiguration("0 * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap1, jobConfig1);
        final JobSchedulingConfiguration jsc2 = new JobSchedulingConfiguration("* 0 * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap2, jobConfig2);
        final JobSchedulingConfiguration jsc3 = new JobSchedulingConfiguration("* * 0 * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap3, jobConfig3);

        assertNotNull(jsc1);
        assertNotNull(jsc2);
        assertNotNull(jsc3);

        assertEquals(false, jsc1.equals(jsc2));
        assertEquals(true, jsc1.equals(jsc3));
        assertEquals(false, jsc2.equals(jsc1));
        assertEquals(false, jsc2.equals(jsc3));
        assertEquals(true, jsc3.equals(jsc1));
        assertEquals(false, jsc3.equals(jsc2));

    }

    @Test
    public void testIsEqualJobClassDifferent() {
        final Map<String, String> dataMap1 = new HashMap<String, String>();
        final Map<String, String> dataMap2 = new HashMap<String, String>();
        dataMap2.put("SomeKey", "SomeValue");

        final Map<String, String> dataMap3 = new HashMap<String, String>();
        final JobConfig jobConfig = new JobConfig(Sets.newHashSet("*"), 1000, 50, true, null);
        final JobSchedulingConfiguration jsc1 = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap1, jobConfig);
        final JobSchedulingConfiguration jsc2 = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap2, jobConfig);
        final JobSchedulingConfiguration jsc3 = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap3, jobConfig);

        assertNotNull(jsc1);
        assertNotNull(jsc2);
        assertNotNull(jsc3);

        assertEquals(false, jsc1.isEqual(jsc2));
        assertEquals(true, jsc1.isEqual(jsc3));
        assertEquals(false, jsc2.isEqual(jsc1));
        assertEquals(false, jsc2.isEqual(jsc3));
        assertEquals(true, jsc3.isEqual(jsc1));
        assertEquals(false, jsc3.isEqual(jsc2));
    }

    @Test
    public void testIsEqualJobDataMapDifferent() {
        final Map<String, String> dataMap1 = new HashMap<String, String>();
        final Map<String, String> dataMap2 = new HashMap<String, String>();
        dataMap2.put("SomeKey", "SomeValue");

        final Map<String, String> dataMap3 = new HashMap<String, String>();
        final JobConfig jobConfig = new JobConfig(Sets.newHashSet("*"), 1000, 50, true, null);
        final JobSchedulingConfiguration jsc1 = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap1, jobConfig);
        final JobSchedulingConfiguration jsc2 = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap2, jobConfig);
        final JobSchedulingConfiguration jsc3 = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap3, jobConfig);

        assertNotNull(jsc1);
        assertNotNull(jsc2);
        assertNotNull(jsc3);

        assertEquals(false, jsc1.isEqual(jsc2));
        assertEquals(true, jsc1.isEqual(jsc3));
        assertEquals(false, jsc2.isEqual(jsc1));
        assertEquals(false, jsc2.isEqual(jsc3));
        assertEquals(true, jsc3.isEqual(jsc1));
        assertEquals(false, jsc3.isEqual(jsc2));
    }

    @Test
    public void testIsEqualCronExpressionDifferent() {
        final Map<String, String> dataMap1 = new HashMap<String, String>();
        final Map<String, String> dataMap2 = new HashMap<String, String>();
        dataMap2.put("SomeKey", "SomeValue");

        final Map<String, String> dataMap3 = new HashMap<String, String>();
        final JobConfig jobConfig = new JobConfig(Sets.newHashSet("*"), 1000, 50, true, null);
        final JobSchedulingConfiguration jsc1 = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap1, jobConfig);
        final JobSchedulingConfiguration jsc2 = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap2, jobConfig);
        final JobSchedulingConfiguration jsc3 = new JobSchedulingConfiguration("0 * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap3, jobConfig);

        assertNotNull(jsc1);
        assertNotNull(jsc2);
        assertNotNull(jsc3);

        assertEquals(false, jsc1.isEqual(jsc2));
        assertEquals(false, jsc1.isEqual(jsc3));
        assertEquals(false, jsc2.isEqual(jsc1));
        assertEquals(false, jsc2.isEqual(jsc3));
        assertEquals(false, jsc3.isEqual(jsc1));
        assertEquals(false, jsc3.isEqual(jsc2));
    }

    @Test
    public void testIsEqualStartDelayMillisDifferent() {
        final Map<String, String> dataMap1 = new HashMap<String, String>();
        final Map<String, String> dataMap2 = new HashMap<String, String>();
        dataMap2.put("SomeKey", "SomeValue");

        final Map<String, String> dataMap3 = new HashMap<String, String>();
        final JobConfig jobConfig = new JobConfig(Sets.newHashSet("*"), 1000, 50, true, null);
        final JobSchedulingConfiguration jsc1 = new JobSchedulingConfiguration(10000, 20000,
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap1, jobConfig);
        final JobSchedulingConfiguration jsc2 = new JobSchedulingConfiguration(10001, 20000,
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap3, jobConfig);
        final JobSchedulingConfiguration jsc3 = new JobSchedulingConfiguration(10000, 20000,
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap3, jobConfig);

        assertNotNull(jsc1);
        assertNotNull(jsc2);
        assertNotNull(jsc3);

        assertEquals(false, jsc1.isEqual(jsc2));
        assertEquals(true, jsc1.isEqual(jsc3));
        assertEquals(false, jsc2.isEqual(jsc1));
        assertEquals(false, jsc2.isEqual(jsc3));
        assertEquals(true, jsc3.isEqual(jsc1));
        assertEquals(false, jsc3.isEqual(jsc2));
    }

    @Test
    public void testIsEqualIntervalMillisDifferent() {
        final Map<String, String> dataMap1 = new HashMap<String, String>();
        final Map<String, String> dataMap2 = new HashMap<String, String>();
        dataMap2.put("SomeKey", "SomeValue");

        final Map<String, String> dataMap3 = new HashMap<String, String>();
        final JobConfig jobConfig = new JobConfig(Sets.newHashSet("*"), 1000, 50, true, null);
        final JobSchedulingConfiguration jsc1 = new JobSchedulingConfiguration(10000, 20000,
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap1, jobConfig);
        final JobSchedulingConfiguration jsc2 = new JobSchedulingConfiguration(10000, 20001,
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap3, jobConfig);
        final JobSchedulingConfiguration jsc3 = new JobSchedulingConfiguration(10000, 20000,
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap3, jobConfig);

        assertNotNull(jsc1);
        assertNotNull(jsc2);
        assertNotNull(jsc3);

        assertEquals(false, jsc1.isEqual(jsc2));
        assertEquals(true, jsc1.isEqual(jsc3));
        assertEquals(false, jsc2.isEqual(jsc1));
        assertEquals(false, jsc2.isEqual(jsc3));
        assertEquals(true, jsc3.isEqual(jsc1));
        assertEquals(false, jsc3.isEqual(jsc2));
    }

    @Test
    public void testIsEqualJobConfigDifferent() {
        final Map<String, String> dataMap1 = new HashMap<String, String>();
        final Map<String, String> dataMap2 = new HashMap<String, String>();
        dataMap2.put("SomeKey", "SomeValue");

        final Map<String, String> dataMap3 = new HashMap<String, String>();
        final JobConfig jobConfigAllowedAppInstanceAltered = new JobConfig(Sets.newHashSet("local_local"), 1000, 50,
                true, null);
        final JobSchedulingConfiguration jscAllowedAppInstanceAltered = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap1, jobConfigAllowedAppInstanceAltered);

        final JobConfig jobConfigLimitAltered = new JobConfig(Sets.newHashSet("*"), 100, 50, true, null);
        final JobSchedulingConfiguration jscLimitAltered = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap1, jobConfigLimitAltered);

        final JobConfig jobConfigStartupLimitAltered = new JobConfig(Sets.newHashSet("*"), 1000, 5, true, null);
        final JobSchedulingConfiguration jscStartupLimitAltered = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap1, jobConfigStartupLimitAltered);

        final JobConfig jobConfigActiveAltered = new JobConfig(Sets.newHashSet("*"), 1000, 5, false, null);
        final JobSchedulingConfiguration jscActiveAltered = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap1, jobConfigActiveAltered);

        final JobConfig jobConfigGroupAltered = new JobConfig(Sets.newHashSet("*"), 1000, 5, false,
                new JobGroupConfig("JobGroup", true, null));
        final JobSchedulingConfiguration jscGroupAltered = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap1, jobConfigGroupAltered);

        final JobConfig jobConfig1 = new JobConfig(Sets.newHashSet("*"), 1000, 50, true, null);
        final JobSchedulingConfiguration jsc1 = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "SomeJobDescription", dataMap1, jobConfig1);

        final JobConfig jobConfig2 = new JobConfig(Sets.newHashSet("*"), 1000, 50, true, null);
        final JobSchedulingConfiguration jsc2 = new JobSchedulingConfiguration("* * * * * ?",
                TestJob1Job.class.getName(), "Some JobDescription", dataMap3, jobConfig2);

        assertNotNull(jsc1);
        assertNotNull(jsc2);

        assertEquals(true, jsc1.isEqual(jsc2));
        assertEquals(true, jsc2.isEqual(jsc1));

        assertEquals(false, jsc1.isEqual(jscAllowedAppInstanceAltered));
        assertEquals(false, jsc2.isEqual(jscAllowedAppInstanceAltered));

        assertEquals(false, jsc1.isEqual(jscLimitAltered));
        assertEquals(false, jsc2.isEqual(jscLimitAltered));

        assertEquals(false, jsc1.isEqual(jscStartupLimitAltered));
        assertEquals(false, jsc2.isEqual(jscStartupLimitAltered));

        assertEquals(false, jsc1.isEqual(jscActiveAltered));
        assertEquals(false, jsc2.isEqual(jscActiveAltered));

        assertEquals(false, jsc1.isEqual(jscGroupAltered));
        assertEquals(false, jsc2.isEqual(jscGroupAltered));

    }
}
