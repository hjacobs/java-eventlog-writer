package de.zalando.zomcat.jobs.management;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;

import org.quartz.JobDetail;

import de.zalando.zomcat.jobs.management.impl.AbstractJobManager;
import de.zalando.zomcat.jobs.management.impl.SingleQuartzSchedulerJobManager;
import de.zalando.zomcat.jobs.management.impl.SingleQuartzSchedulerJobManagerManagedJob;

/**
 * Unittest for {@link AbstractJobManager} - tests internal Methods of {@link AbstractJobManager}.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public class AbstractJobManagerTest {

    @Test
    public void testIsJobMatchesQuartzJobDetailNameAndGroup() throws SecurityException, NoSuchMethodException,
        JobManagerException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        final Method privateMethod = AbstractJobManager.class.getDeclaredMethod(
                "isJobMatchesQuartzJobDetailNameAndGroup", JobManagerManagedJob.class, String.class, String.class);
        privateMethod.setAccessible(true);

        final SingleQuartzSchedulerJobManager jobManager = new SingleQuartzSchedulerJobManager();

        final JobDetail jobDetailSet = new JobDetail("SomeJobName", "SomeJobGroup", TestJob1Job.class);
        final JobManagerManagedJob jmmj = new SingleQuartzSchedulerJobManagerManagedJob(null, jobDetailSet, null, null,
                1);

        Object retVal = privateMethod.invoke(jobManager, jmmj, "SomeJobName", "SomeJobGroup");

        assertNotNull(retVal);
        assertEquals(true, retVal);

        retVal = privateMethod.invoke(jobManager, jmmj, "SomeJobName1", "SomeJobGroup");

        assertNotNull(retVal);
        assertEquals(false, retVal);

        retVal = privateMethod.invoke(jobManager, jmmj, "SomeJobName", "SomeJobGroup2");

        assertNotNull(retVal);
        assertEquals(false, retVal);

        retVal = privateMethod.invoke(jobManager, jmmj, "SomeJobName1", "SomeJobGroup2");

        assertNotNull(retVal);
        assertEquals(false, retVal);
    }
}
