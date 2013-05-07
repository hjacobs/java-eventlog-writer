package de.zalando.zomcat.jobs.lock;

public interface LockResourceManager {

    boolean acquireLock(String lockingComponent, String resource, String flowId, long expectedMaximumDuration);

    void releaseLock(String resource, String flowId);

    boolean peekLock(String resource);
}
