package de.zalando.zomcat.jobs.lock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:backendContextTest.xml"})
public class LockResourceManagerIT {

    private static final String FLOWID = "flowid";
    private static final String TEST_COMPONENT = "test_component";
    private static final String TEST_RESOURCE = "test_resource";

    private static final int CONCURRENT_CLIENTS = 20;
    private static final int CLIENT_INVOCATIONS = 20;

    // timeout in milliseconds
    private static final long CONCURRENT_EXECUTION_TIMEOUT = 5000;

    @Autowired
    private LockResourceManagerImpl lockResourceManager;

    @Before
    public void setUp() {
        lockResourceManager.releaseLock(TEST_RESOURCE, FLOWID);
    }

    @Test
    public void lockFreeResourceTest() {
        final boolean acquired = lockResourceManager.acquireLock(TEST_COMPONENT, TEST_RESOURCE, FLOWID);
        Assert.assertTrue("should have acquired lock", acquired);

    }

    @Test
    public void tryToLockLockedResourceTest() {
        final boolean acquired = lockResourceManager.acquireLock(TEST_COMPONENT, TEST_RESOURCE, FLOWID);
        Assert.assertTrue("should have acquired lock", acquired);

        final boolean notAcquired = lockResourceManager.acquireLock(TEST_COMPONENT, TEST_RESOURCE, FLOWID);
        Assert.assertFalse("should not have acquired the lock", notAcquired);
    }

    @Test
    public void tryToUnlockUnlockedResourceTest() {
        lockResourceManager.releaseLock(TEST_RESOURCE, FLOWID);
    }

    @Test
    public void unlockResourceTest() {

        boolean acquired = lockResourceManager.acquireLock(TEST_COMPONENT, TEST_RESOURCE, FLOWID);
        Assert.assertTrue("should have acquired lock", acquired);

        lockResourceManager.releaseLock(TEST_RESOURCE, FLOWID);

        acquired = lockResourceManager.acquireLock(TEST_COMPONENT, TEST_RESOURCE, FLOWID);
        Assert.assertTrue("should have acquired lock", acquired);
    }

    @Test
    public void peekResource() {

        boolean peeked = lockResourceManager.peekLock(TEST_RESOURCE);
        Assert.assertFalse("should not be locked.", peeked);

        final boolean acquired = lockResourceManager.acquireLock(TEST_COMPONENT, TEST_RESOURCE, FLOWID);
        Assert.assertTrue("should have acquired lock", acquired);

        peeked = lockResourceManager.peekLock(TEST_RESOURCE);
        Assert.assertTrue("should not be locked.", peeked);
    }

    @Test
    public void testReconnect() {

        final boolean acquired = lockResourceManager.acquireLock(TEST_COMPONENT, TEST_RESOURCE, FLOWID);
        Assert.assertTrue("should have acquired lock", acquired);

        // DEBUG TEST: set breakpopints 1 and 2

        // bp 1
        lockResourceManager.releaseLock(TEST_RESOURCE, FLOWID);
        // stop DB, run until next bp

        // Up to here, resource is locked on DB, DB is not reachable.
        // Now, we will simulate that network is up and working again:

        // bp 2
        boolean locked = true;
        // restart DB and run

        while (locked) {
            try {
                locked = lockResourceManager.peekLock(TEST_RESOURCE);
            } catch (Exception e) { }

        }
    }

    @Test
    public void concurrentlyLockFreeResourceTest() throws Exception {

        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_CLIENTS);

        Collection<Callable<Boolean>> callableCollection = new ArrayList<Callable<Boolean>>(CONCURRENT_CLIENTS);
        for (int i = 0; i < CONCURRENT_CLIENTS; i++) {
            callableCollection.add(new Callable<Boolean>() {

                    @Override
                    public Boolean call() throws Exception {
                        return lockResourceManager.acquireLock(TEST_COMPONENT, TEST_RESOURCE, FLOWID);
                    }
                });
        }

        for (int i = 0; i < CLIENT_INVOCATIONS; i++) {

            List<Future<Boolean>> results = executorService.invokeAll(callableCollection, CONCURRENT_EXECUTION_TIMEOUT,
                    TimeUnit.MILLISECONDS);

            int acquiredLocks = 0;
            for (Future<Boolean> future : results) {
                if (future.get()) {
                    acquiredLocks++;
                }
            }

            Assert.assertEquals("should have acquired exactly 1 lock", 1, acquiredLocks);
            lockResourceManager.releaseLock(TEST_RESOURCE, FLOWID);
        }
    }
}
