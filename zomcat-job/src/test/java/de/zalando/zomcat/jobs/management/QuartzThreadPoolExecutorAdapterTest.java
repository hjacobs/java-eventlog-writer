package de.zalando.zomcat.jobs.management;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import de.zalando.zomcat.jobs.management.impl.QuartzThreadPoolExecutorAdapter;

import junit.framework.Assert;

/**
 * Unit tests.
 *
 * @author  pribeiro
 */
public class QuartzThreadPoolExecutorAdapterTest {

    @Test
    public void testDefaultSettings() throws Exception {

        QuartzThreadPoolExecutorAdapter pool = new QuartzThreadPoolExecutorAdapter();
        try {
            pool.initialize();
        } finally {
            pool.shutdown(false);
        }
    }

    @Test(timeout = 60000)
    public void testRunJob() throws Exception {

        final QuartzThreadPoolExecutorAdapter pool = new QuartzThreadPoolExecutorAdapter();
        pool.setMaximumPoolSize(2);
        pool.initialize();

        try {
            Assert.assertEquals(2, pool.blockForAvailableThreads());

            final CountDownLatch ran = new CountDownLatch(1);
            Assert.assertTrue(pool.runInThread(new Runnable() {

                        @Override
                        public void run() {
                            ran.countDown();
                        }
                    }));

            // wait for execution
            ran.await();

            Assert.assertEquals(0, ran.getCount());
        } finally {
            pool.shutdown(true);
        }
    }

    @Test(timeout = 60000)
    public void testRunMaxJobs() throws Exception {
        final QuartzThreadPoolExecutorAdapter pool = new QuartzThreadPoolExecutorAdapter();
        pool.setMaximumPoolSize(2);
        pool.initialize();

        final CountDownLatch sync = new CountDownLatch(2);
        final AtomicInteger counter = new AtomicInteger();

        final class WaitJob implements Runnable {

            private final CountDownLatch lock = new CountDownLatch(1);

            @Override
            public void run() {
                counter.incrementAndGet();
                sync.countDown();
                try {
                    lock.await();
                } catch (InterruptedException e) {
                    Assert.fail(e.getMessage());
                }
            }

            public void unlock() {
                lock.countDown();
            }
        }

        try {
            WaitJob job1 = new WaitJob();
            WaitJob job2 = new WaitJob();
            WaitJob job3 = new WaitJob();

            Assert.assertEquals(2, pool.blockForAvailableThreads());
            Assert.assertTrue(pool.runInThread(job1));

            Assert.assertEquals(1, pool.blockForAvailableThreads());
            Assert.assertTrue(pool.runInThread(job2));

            // make sure that both threads are running in parallel
            sync.await();
            Assert.assertEquals(2, counter.get());

            // thread pool is full. Unlock job1 and run other job
            job1.unlock();

            Assert.assertEquals(1, pool.blockForAvailableThreads());
            Assert.assertTrue(pool.runInThread(job3));

            // unlock the other jobs
            job2.unlock();
            job3.unlock();

        } finally {
            pool.shutdown(true);
        }
    }

    @Test(timeout = 60000)
    public void testRunMultipleJobs() throws Exception {

        final AtomicInteger counter = new AtomicInteger();

        final Runnable job = new Runnable() {

            @Override
            public void run() {
                try {
                    counter.incrementAndGet();

                    // sleep to simulate a job
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Assert.fail(e.getMessage());
                }
            }
        };

        final QuartzThreadPoolExecutorAdapter pool = new QuartzThreadPoolExecutorAdapter();
        pool.setMaximumPoolSize(5);
        pool.initialize();

        final int jobs = 2000;

        try {

            for (int i = 0; i < jobs; i++) {
                Assert.assertTrue(pool.blockForAvailableThreads() > 0);
                Assert.assertTrue(pool.runInThread(job));
            }

            // Assert.assertEquals(jobs, counter.get());
        } finally {
            pool.shutdown(true);
            Assert.assertEquals(jobs, counter.get());
        }
    }

    @Test(timeout = 60000)
    public void testBlockForAvailableThreads() throws Exception {
        final QuartzThreadPoolExecutorAdapter pool = new QuartzThreadPoolExecutorAdapter();
        pool.setMaximumPoolSize(1);
        pool.initialize();

        final Semaphore semaphore = new Semaphore(0);
        final AtomicInteger counter = new AtomicInteger();

        try {
            Assert.assertEquals(1, pool.blockForAvailableThreads());
            Assert.assertTrue(pool.runInThread(new Runnable() {

                        @Override
                        public void run() {
                            semaphore.release();

                            try {

                                // Seems like a code smell, because we can't guarantee that method
                                // blockForAvailableThreads() will be called while this method is being executed
                                // (sleeping).
                                // Still, if there is something wrong on the thread pool this test will eventually fail
                                Thread.sleep(2000);
                                counter.incrementAndGet();
                            } catch (InterruptedException e) {
                                Assert.fail(e.getMessage());
                            }
                        }
                    }));

            // wait for the job
            semaphore.acquire();

            Assert.assertEquals(1, pool.blockForAvailableThreads());
            Assert.assertEquals(1, counter.get());

            Assert.assertTrue(pool.runInThread(new Runnable() {

                        @Override
                        public void run() {
                            counter.incrementAndGet();
                            semaphore.release();
                        }
                    }));

            semaphore.acquire();

            // check if job ran
            Assert.assertEquals(2, counter.get());
        } finally {
            pool.shutdown(true);
        }
    }

    @Test(timeout = 60000)
    public void testWaitShutdownWithTaskRunning() throws Exception {
        shutdownWithTaskRunning(true);
    }

    @Test(timeout = 60000)
    public void testForceShutdownWithTaskRunning() throws Exception {
        shutdownWithTaskRunning(false);
    }

    private void shutdownWithTaskRunning(final boolean waitForJobsToComplete) throws Exception {
        final QuartzThreadPoolExecutorAdapter pool = new QuartzThreadPoolExecutorAdapter();
        pool.setMaximumPoolSize(1);
        pool.setKeepAliveTime(0);
        pool.setShutdownTimeout(1);
        pool.initialize();

        try {
            final CountDownLatch mainLock = new CountDownLatch(1);
            final CountDownLatch jobLock = new CountDownLatch(1);

            Assert.assertEquals(1, pool.blockForAvailableThreads());
            Assert.assertTrue(pool.runInThread(new Runnable() {

                        @Override
                        public void run() {

                            // unlock main thread
                            mainLock.countDown();
                            try {

                                // wait forever
                                jobLock.await();
                                Assert.fail();
                            } catch (InterruptedException e) {
                                Assert.fail(e.getMessage());
                            }
                        }
                    }));

            // certify that the task is running
            mainLock.await();
        } finally {
            pool.shutdown(waitForJobsToComplete);
        }
    }
}
