package de.zalando.zomcat.jobs.lock;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.dao.DataAccessResourceFailureException;

import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

import de.zalando.sprocwrapper.AbstractSProcService;
import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;

@Service("lockResourceManager")
public class LockResourceManagerImpl extends AbstractSProcService<LockResourceSprocService, DataSourceProvider>
    implements LockResourceSprocService {

    private static final Logger LOG = LoggerFactory.getLogger(LockResourceManagerImpl.class);
    private static final int DEFAULT_EXPECTED_MAXIMUM_DURATION = 1000 * 60; // 1
                                                                            // min.

    private static final int DEFAULT_RETRIES_PER_MINUTE = 12; // every 5 seconds
    private static final int DEFAULT_RETRY_TIME = 2;          // in minutes;

    private AtomicBoolean unlocked = new AtomicBoolean(false);
    private AtomicInteger counter;

    private ScheduledExecutorService scheduler;

    public int getDefaultRetriesPerMinute() {
        return DEFAULT_RETRIES_PER_MINUTE;
    }

    public int getDefaultRetryTime() {
        return DEFAULT_RETRY_TIME;
    }

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
        Preconditions.checkNotNull(resource, "resource can't be null");
        Preconditions.checkNotNull(flowId, " flowId can't be null");
        try {
            sproc.releaseLock(resource, flowId);
        } catch (DataAccessResourceFailureException e) {
            Unlock unlockTask = new Unlock(resource, getDefaultRetryTime() * getDefaultRetriesPerMinute(), flowId);

            ScheduledExecutorService schedulerUnlock = Executors.newScheduledThreadPool(1);
            schedulerUnlock.scheduleAtFixedRate(unlockTask, 0, 60 / getDefaultRetriesPerMinute(), TimeUnit.SECONDS); // execute every 5 seconds for 2 minutes

            scheduler = schedulerUnlock;

        }

    }

    @Override
    public boolean peekLock(final String resource) {
        return sproc.peekLock(resource);
    }

    // Unlock runnable class
    private class Unlock implements Runnable {
        private String resource;
        private String flow;

        Unlock(final String resource, final int attemps, final String flowId) {
            this.resource = resource;

            this.flow = flowId;
            counter = new AtomicInteger(attemps);
        }

        public void run() {

            if (counter.getAndDecrement() > 0 && !unlocked.get()) {

                try {
                    LOG.info("Releasing lock on {}", resource);
                    sproc.releaseLock(resource, flow);

                    unlocked.set(true);
                    scheduler.shutdown();
                } catch (Exception e) {
                    LOG.info("Release failed {}", resource);
                }

            } else { // finish scheduler
                scheduler.shutdown();
            }

        }

    }

}
