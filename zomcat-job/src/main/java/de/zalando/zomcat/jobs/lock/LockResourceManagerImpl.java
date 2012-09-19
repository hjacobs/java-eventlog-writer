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
    private static final int DEFAULT_EXPECTED_MAXIMUM_DURATION = 1000 * 60; // 1
                                                                            // min.

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
    public void releaseLock(final String resource) {
        LOG.info("Releasing lock on {}", resource);
        sproc.releaseLock(resource);
    }

    @Override
    public boolean peekLock(final String resource) {
        return sproc.peekLock(resource);
    }
}
