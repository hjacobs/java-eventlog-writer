package de.zalando.zomcat.util.jobs;

import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.JobGroupConfig;

import junit.framework.Assert;

public class JobConfigTest {

    private static final String CURRENT_APP_INSTANCE_KEY = "currentAppInstanceKey";

    private static final String OTHER_APP_INSTANCE_KEY = "otherAppInstanceKey";

    private static final String ALL_APP_INSTANCE_KEY_ASTERISK = "*";

    @Test
    public void testJobConfigIsAllowedAppInstanceKeyValidJobConfigAppInstanceKeyOnly() {
        final Set<String> allowedJobGroupAppInstances = Sets.newHashSet();
        final Set<String> allowedJobAppInstances = Sets.newHashSet();
        allowedJobAppInstances.add(CURRENT_APP_INSTANCE_KEY);

        final JobGroupConfig jobGroupConfig = new JobGroupConfig("TestJobGroup", true, allowedJobGroupAppInstances);
        final JobConfig jobConfig = new JobConfig(allowedJobAppInstances, 0, 0, true, jobGroupConfig);

        Assert.assertEquals(true, jobConfig.isAllowedAppInstanceKey(CURRENT_APP_INSTANCE_KEY));
    }

    @Test
    public void testJobConfigIsAllowedAppInstanceKeyValidJobGroupConfigAppInstanceKeyOnly() {
        final Set<String> allowedJobGroupAppInstances = Sets.newHashSet();
        allowedJobGroupAppInstances.add(CURRENT_APP_INSTANCE_KEY);

        final Set<String> allowedJobAppInstances = Sets.newHashSet();

        final JobGroupConfig jobGroupConfig = new JobGroupConfig("TestJobGroup", true, allowedJobGroupAppInstances);
        final JobConfig jobConfig = new JobConfig(allowedJobAppInstances, 0, 0, true, jobGroupConfig);

        Assert.assertEquals(true, jobConfig.isAllowedAppInstanceKey(CURRENT_APP_INSTANCE_KEY));
    }

    @Test
    public void testJobConfigIsAllowedAppInstanceKeyInvalidJobConfigAppInstanceKeyOnly() {
        final Set<String> allowedJobGroupAppInstances = Sets.newHashSet();
        final Set<String> allowedJobAppInstances = Sets.newHashSet();
        allowedJobAppInstances.add(OTHER_APP_INSTANCE_KEY);

        final JobGroupConfig jobGroupConfig = new JobGroupConfig("TestJobGroup", true, allowedJobGroupAppInstances);
        final JobConfig jobConfig = new JobConfig(allowedJobAppInstances, 0, 0, true, jobGroupConfig);

        Assert.assertEquals(false, jobConfig.isAllowedAppInstanceKey(CURRENT_APP_INSTANCE_KEY));
    }

    @Test
    public void testJobConfigIsAllowedAppInstanceKeyInvalidJobGroupConfigAppInstanceKeyOnly() {
        final Set<String> allowedJobGroupAppInstances = Sets.newHashSet();
        allowedJobGroupAppInstances.add(OTHER_APP_INSTANCE_KEY);

        final Set<String> allowedJobAppInstances = Sets.newHashSet();

        final JobGroupConfig jobGroupConfig = new JobGroupConfig("TestJobGroup", true, allowedJobGroupAppInstances);
        final JobConfig jobConfig = new JobConfig(allowedJobAppInstances, 0, 0, true, jobGroupConfig);

        Assert.assertEquals(false, jobConfig.isAllowedAppInstanceKey(CURRENT_APP_INSTANCE_KEY));
    }

    @Test
    public void testJobConfigIsAllowedAppInstanceKeyValidJobGroupConfigAppInstanceKeyInvalidJobConfigAppInstanceKey() {
        final Set<String> allowedJobGroupAppInstances = Sets.newHashSet();
        allowedJobGroupAppInstances.add(CURRENT_APP_INSTANCE_KEY);

        final Set<String> allowedJobAppInstances = Sets.newHashSet();
        allowedJobAppInstances.add(OTHER_APP_INSTANCE_KEY);

        final JobGroupConfig jobGroupConfig = new JobGroupConfig("TestJobGroup", true, allowedJobGroupAppInstances);
        final JobConfig jobConfig = new JobConfig(allowedJobAppInstances, 0, 0, true, jobGroupConfig);

        Assert.assertEquals(false, jobConfig.isAllowedAppInstanceKey(CURRENT_APP_INSTANCE_KEY));
    }

    @Test
    public void testJobConfigIsAllowedAppInstanceKeyValidJobGroupConfigAppInstanceKeyAllJobConfigAppInstanceKey() {
        final Set<String> allowedJobGroupAppInstances = Sets.newHashSet();
        allowedJobGroupAppInstances.add(CURRENT_APP_INSTANCE_KEY);

        final Set<String> allowedJobAppInstances = Sets.newHashSet();
        allowedJobAppInstances.add(ALL_APP_INSTANCE_KEY_ASTERISK);

        final JobGroupConfig jobGroupConfig = new JobGroupConfig("TestJobGroup", true, allowedJobGroupAppInstances);
        final JobConfig jobConfig = new JobConfig(allowedJobAppInstances, 0, 0, true, jobGroupConfig);

        Assert.assertEquals(true, jobConfig.isAllowedAppInstanceKey(CURRENT_APP_INSTANCE_KEY));
    }

    @Test
    public void testJobConfigIsAllowedAppInstanceKeyAllJobGroupConfigAppInstanceKeyValidJobConfigAppInstanceKeys() {
        final Set<String> allowedJobGroupAppInstances = Sets.newHashSet();
        allowedJobGroupAppInstances.add(ALL_APP_INSTANCE_KEY_ASTERISK);

        final Set<String> allowedJobAppInstances = Sets.newHashSet();
        allowedJobAppInstances.add(CURRENT_APP_INSTANCE_KEY);

        final JobGroupConfig jobGroupConfig = new JobGroupConfig("TestJobGroup", true, allowedJobGroupAppInstances);
        final JobConfig jobConfig = new JobConfig(allowedJobAppInstances, 0, 0, true, jobGroupConfig);

        Assert.assertEquals(true, jobConfig.isAllowedAppInstanceKey(CURRENT_APP_INSTANCE_KEY));
    }

    @Test
    public void testJobConfigIsAllowedAppInstanceKeyAllJobGroupConfigAppInstanceKeyInvalidJobConfigAppInstanceKeys() {
        final Set<String> allowedJobGroupAppInstances = Sets.newHashSet();
        allowedJobGroupAppInstances.add(ALL_APP_INSTANCE_KEY_ASTERISK);

        final Set<String> allowedJobAppInstances = Sets.newHashSet();
        allowedJobAppInstances.add(OTHER_APP_INSTANCE_KEY);

        final JobGroupConfig jobGroupConfig = new JobGroupConfig("TestJobGroup", true, allowedJobGroupAppInstances);
        final JobConfig jobConfig = new JobConfig(allowedJobAppInstances, 0, 0, true, jobGroupConfig);

        Assert.assertEquals(false, jobConfig.isAllowedAppInstanceKey(CURRENT_APP_INSTANCE_KEY));
    }

    @Test
    public void testJobConfigIsAllowedAppInstanceKeyAllJobGroupConfigAppInstanceKey() {
        final Set<String> allowedJobGroupAppInstances = Sets.newHashSet();
        allowedJobGroupAppInstances.add(ALL_APP_INSTANCE_KEY_ASTERISK);

        final Set<String> allowedJobAppInstances = Sets.newHashSet();

        final JobGroupConfig jobGroupConfig = new JobGroupConfig("TestJobGroup", true, allowedJobGroupAppInstances);
        final JobConfig jobConfig = new JobConfig(allowedJobAppInstances, 0, 0, true, jobGroupConfig);

        Assert.assertEquals(true, jobConfig.isAllowedAppInstanceKey(CURRENT_APP_INSTANCE_KEY));
    }
}
