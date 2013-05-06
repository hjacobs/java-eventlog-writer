package de.zalando.zomcat.jobs.lock;

import java.sql.SQLException;

import org.easymock.EasyMock;

import org.junit.Test;

import org.springframework.dao.DataAccessException;

import org.springframework.jdbc.CannotGetJdbcConnectionException;

import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;

/**
 * @author  hjacobs
 */
public class LockResourceManagerImplTest {

    private static final String TEST_RESOURCE = "test_resource";
    private static final String TEST_FLOWID = "test_flowid";

    @Test(expected = IllegalArgumentException.class)
    public void testWithoutDataSourceProvider() {
        final LockResourceSprocService svc = new LockResourceManagerImpl(null);
    }

    @Test
    public void testReleaseLockRecovery() {
        LockResourceSprocService sproc = EasyMock.createMock(LockResourceSprocService.class);

        // fail on first attempt
        sproc.releaseLock(TEST_RESOURCE, TEST_FLOWID);
        EasyMock.expectLastCall()
                .andThrow(new CannotGetJdbcConnectionException("Release lock recovery test", (SQLException) null))
                .once();

        // succeed on second attempt
        sproc.releaseLock(TEST_RESOURCE, TEST_FLOWID);
        EasyMock.expectLastCall().once();

        DataSourceProvider ds = EasyMock.createMock(DataSourceProvider.class);

        EasyMock.replay(sproc, ds);

        final LockResourceManagerImpl svc = new MockLockResourceManagerImpl(ds, sproc);
        svc.releaseLock(TEST_RESOURCE, TEST_FLOWID);

        EasyMock.verify(sproc, ds);
    }

    @Test(expected = DataAccessException.class)
    public void testReleaseLockFailure() throws Exception {

        LockResourceSprocService sproc = EasyMock.createMock(LockResourceSprocService.class);
        sproc.releaseLock(TEST_RESOURCE, TEST_FLOWID);

        // just throw an exception every time we try to release the lock
        EasyMock.expectLastCall()
                .andThrow(new CannotGetJdbcConnectionException("Release lock recovery test", (SQLException) null))
                .anyTimes();

        DataSourceProvider ds = EasyMock.createMock(DataSourceProvider.class);

        EasyMock.replay(sproc, ds);

        final LockResourceManagerImpl svc = new MockLockResourceManagerImpl(ds, sproc);
        svc.releaseLock(TEST_RESOURCE, TEST_FLOWID);

        EasyMock.verify(sproc, ds);
    }

    /**
     * Mock implementation used to inject our own sproc instance in order to simulate connection problems.
     *
     * @author  pribeiro
     */
    private static final class MockLockResourceManagerImpl extends LockResourceManagerImpl {

        public MockLockResourceManagerImpl(final DataSourceProvider provider, final LockResourceSprocService sproc) {
            super(provider);
            this.sproc = sproc;
        }

        protected int getRetryTime() {

            // make it faster, just wait one millisecond
            return 1;
        }
    }

}
