package de.zalando.zomcat.jobs.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import de.zalando.sprocwrapper.AbstractSProcService;
import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;

@Service("lockResourceManager")
public class LockResourceManagerImpl extends AbstractSProcService<LockResourceSprocService, DataSourceProvider>
    implements LockResourceSprocService {

    private static final Logger LOG = LoggerFactory.getLogger(LockResourceManagerImpl.class);
    private static final int DEFAULT_EXPECTED_MAXIMUM_DURATION = 1000 * 60; // 1 min.

    /**
     * Retry time in milliseconds.
     */
    private static final int DEFAULT_RETRY_TIME = 1000 * 5; // 5 seconds;

    /**
     * Default number of retries.
     */
    private static final int DEFAULT_NUMBER_RETRIES = 24; // 2 minutes;

    @Autowired
    public LockResourceManagerImpl(@Qualifier("resourceLockDataSourceProvider") final DataSourceProvider provider) {
        super(provider, LockResourceSprocService.class);
    }

    @Override
    public boolean acquireLock(final String lockingComponent, final String resource, final String flowId) {
        return acquireLock(lockingComponent, resource, flowId, DEFAULT_EXPECTED_MAXIMUM_DURATION);
    }

    /**
     * Returns true if the resource could be locked.
     *
     * @param   resource
     * @param   lockingComponent
     *
     * @return
     */
    @Override
    public boolean acquireLock(final String lockingComponent, final String resource, final String flowId,
            final long expectedMaximumDuration) {

        LOG.info("Acquiring lock on {} for {}", new String[] {resource, lockingComponent});

        return sproc.acquireLock(lockingComponent, resource, flowId, expectedMaximumDuration);

    }

    @Override
    public void releaseLock(final String resource, final String flowId) {
        LOG.info("Releasing lock on {} for {}", resource, flowId);

        boolean retry = true;
        int retryCounter = 0;
        final int retryTime = getRetryTime();
        final int maxRetries = getNumberOfRetries();

        do {
            try {
                sproc.releaseLock(resource, flowId);
                retry = false;
                LOG.info("Lock with resource {} and flow id {} released", resource, flowId);
            } catch (Throwable e) {
                retry = retryCounter++ < maxRetries;
                if (retry) {
                    LOG.warn("Could not release job lock {}. Retrying in {} millis",
                        new Object[] {resource, retryTime, e});

                    try {
                        Thread.sleep(retryTime);
                    } catch (InterruptedException e1) {
                        LOG.warn("Retry sleep interrupted. Retrying to release lock {} now.", resource, e1);
                    }
                } else {
                    LOG.error("Could not release lock {}, max retries exceeded {}. Please remove the lock manually",
                        resource, maxRetries);

                    throw e;
                }
            }
        } while (retry);
    }

    @Override
    public boolean peekLock(final String resource) {
        return sproc.peekLock(resource);
    }

    protected int getNumberOfRetries() {
        return DEFAULT_NUMBER_RETRIES;
    }

    protected int getRetryTime() {
        return DEFAULT_RETRY_TIME;
    }
}
