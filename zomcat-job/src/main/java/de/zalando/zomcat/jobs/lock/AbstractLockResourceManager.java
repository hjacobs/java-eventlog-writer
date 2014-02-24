package de.zalando.zomcat.jobs.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLockResourceManager implements LockResourceManager {

    private static final Logger LOG = LoggerFactory.getLogger(RedisLockResourceManagerImpl.class);

    /**
     * Retry time in milliseconds.
     */
    private static final int DEFAULT_RETRY_TIME = 1000 * 5; // 5 seconds

    /**
     * Default number of retries.
     */
    private static final int DEFAULT_NUMBER_RETRIES = 24; // 2 minutes

    @Override
    public void releaseLock(final String resource, final String flowId) {
        LOG.info("Releasing lock on {} for {}", resource, flowId);

        boolean retry = true;
        int retryCounter = 0;
        final int retryTime = getRetryTime();
        final int maxRetries = getNumberOfRetries();

        do {
            try {
                doReleaseLock(resource, flowId);
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

    protected int getNumberOfRetries() {
        return DEFAULT_NUMBER_RETRIES;
    }

    protected int getRetryTime() {
        return DEFAULT_RETRY_TIME;
    }

    protected abstract void doReleaseLock(final String resource, final String flowId);
}
