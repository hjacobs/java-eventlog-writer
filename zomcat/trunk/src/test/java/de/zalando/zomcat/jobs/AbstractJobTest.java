package de.zalando.zomcat.jobs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Set;

import org.easymock.EasyMock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.quartz.JobExecutionContext;

import org.springframework.context.ApplicationContext;

import com.google.common.collect.Sets;

import de.zalando.zomcat.jobs.lock.LockResourceManager;

/**
 * @author  hjacobs
 */
public class AbstractJobTest {

    class TestJob extends AbstractJob {

        @Override
        public void doRun(final JobExecutionContext context, final JobConfig config) throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String getDescription() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private Method privateMethod;

    @Before
    public void setUp() throws Exception {
        privateMethod = AbstractJob.class.getDeclaredMethod("isJobResourceLocked", String.class);
        privateMethod.setAccessible(true);
    }

    @Test(expected = InvocationTargetException.class)
    public void testIsJobResourceLockedWithoutManager() throws Exception {
        AbstractJob job = new TestJob();
        privateMethod.invoke(job, "MY_RESOURCE");
    }

    @Test(expected = InvocationTargetException.class)
    public void testIsJobResourceLockedWrongName() throws Exception {
        AbstractJob job = new TestJob();
        privateMethod.invoke(job, "stock_service"); // does not follow naming conventions
    }

    @Test
    public void testIsJobResourceLockedWithoutLock() throws Exception {
        AbstractJob job = new TestJob();
        privateMethod.invoke(job, new Object[] {null});
    }

    @Test
    public void testLockWithFlowId() {
        TestLockResourceManager lockResourceManager = new TestLockResourceManager();

        ApplicationContext context = EasyMock.createMock(ApplicationContext.class);
        EasyMock.expect(context.getBean("lockResourceManager", LockResourceManager.class))
                .andReturn(lockResourceManager).once();
        EasyMock.replay(context);

        final Set<String> allowedJobGroupAppInstances = Sets.newHashSet();
        final Set<String> allowedJobAppInstances = Sets.newHashSet();
        final JobGroupConfig jobGroupConfig = new JobGroupConfig("TestJobGroup", true, allowedJobGroupAppInstances);
        final JobConfig jobConfig = new JobConfig(allowedJobAppInstances, 0, 0, true, jobGroupConfig);

        TestResourceLockJob job = new TestResourceLockJob();

        job.setApplicationContext(context);
        job.setJobGroupConfig(jobGroupConfig);
        job.setJobConfig(jobConfig);
        job.executeInternal(null);

        Assert.assertEquals(job.getBeanName(), lockResourceManager.getLockingComponent());
        Assert.assertEquals(job.getBeanName(), lockResourceManager.getAcquireLockResource());
        Assert.assertNotNull(lockResourceManager.getFlowId());
        Assert.assertFalse(lockResourceManager.getFlowId().isEmpty());
        Assert.assertEquals(job.getExpectedMaximumDuration(), lockResourceManager.getExpectedMaximumDuration());
        Assert.assertEquals(1, lockResourceManager.getAcquireLockCounter());

        Assert.assertEquals(job.getBeanName(), lockResourceManager.getReleaseLockResource());
        Assert.assertEquals(1, lockResourceManager.getReleaseLockCounter());

        EasyMock.verify(context);
    }

}
