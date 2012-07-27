package de.zalando.zomcat.jobs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import org.quartz.JobExecutionContext;

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

}
