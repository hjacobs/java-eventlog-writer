package de.zalando.zomcat.jobs;

import de.zalando.zomcat.jobs.lock.LockResourceManager;

public class TestLockResourceManager implements LockResourceManager {

    private String lockingComponent;
    private String acquireLockResource;
    private String flowId;
    private Long expectedMaximumDuration;
    private int acquireLockCounter;

    private String releaseLockResource;
    private int releaseLockCounter;

    private String peekLockResource;
    private int peekLockCounter;

    @Override
    public boolean acquireLock(final String lockingComponent, final String resource, final String flowId) {
        return acquireLock(lockingComponent, resource, flowId, 0);
    }

    @Override
    public boolean acquireLock(final String lockingComponent, final String resource, final String flowId,
            final long expectedMaximumDuration) {
        this.lockingComponent = lockingComponent;
        this.acquireLockResource = resource;
        this.flowId = flowId;
        this.expectedMaximumDuration = expectedMaximumDuration;
        acquireLockCounter++;

        return true;
    }

    @Override
    public void releaseLock(final String resource, final String flowId) {
        if (this.flowId != null && this.flowId.equals(flowId)) {
            this.releaseLockResource = resource;
            releaseLockCounter++;
        }
    }

    @Override
    public boolean peekLock(final String resource) {
        this.peekLockResource = resource;
        peekLockCounter++;

        return true;
    }

    public String getLockingComponent() {
        return lockingComponent;
    }

    public String getAcquireLockResource() {
        return acquireLockResource;
    }

    public String getFlowId() {
        return flowId;
    }

    public Long getExpectedMaximumDuration() {
        return expectedMaximumDuration;
    }

    public int getAcquireLockCounter() {
        return acquireLockCounter;
    }

    public String getReleaseLockResource() {
        return releaseLockResource;
    }

    public int getReleaseLockCounter() {
        return releaseLockCounter;
    }

    public String getPeekLockResource() {
        return peekLockResource;
    }

    public int getPeekLockCounter() {
        return peekLockCounter;
    }
}
