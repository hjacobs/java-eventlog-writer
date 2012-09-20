package de.zalando.zomcat.jobs.lock;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.dao.DuplicateKeyException;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:backendContextTest.xml"})
public class LockResourceManagerIT {

    private static final String FLOWID = "flowid";
    private static final String TEST_COMPONENT = "test_component";
    private static final String TEST_RESOURCE = "test_resource";

    @Autowired
    private LockResourceManagerImpl lockResourceManager;

    @Before
    public void setUp() {
        lockResourceManager.releaseLock(TEST_RESOURCE);
    }

    @Test
    public void lockFreeResourceTest() {

        final boolean acquired = lockResourceManager.acquireLock(TEST_COMPONENT, TEST_RESOURCE, FLOWID);
        Assert.assertTrue("should have acquired lock", acquired);

    }

    @Test(expected = DuplicateKeyException.class)
    public void tryToLockLockedResourceTest() {
        final boolean acquired = lockResourceManager.acquireLock(TEST_COMPONENT, TEST_RESOURCE, FLOWID);
        Assert.assertTrue("should have acquired lock", acquired);

        lockResourceManager.acquireLock(TEST_COMPONENT, TEST_RESOURCE, FLOWID);
        Assert.fail("should not get here");
    }

    @Test
    public void tryToUnlockUnlockedResourceTest() {
        lockResourceManager.releaseLock(TEST_RESOURCE);
    }

    @Test
    public void unlockResourceTest() {

        boolean acquired = lockResourceManager.acquireLock(TEST_COMPONENT, TEST_RESOURCE, FLOWID);
        Assert.assertTrue("should have acquired lock", acquired);

        lockResourceManager.releaseLock(TEST_RESOURCE);

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
}
