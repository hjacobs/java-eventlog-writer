package de.zalando.zomcat.jobs.lock;

public interface LockResourceManager {

    /**
     * @param   resource
     *
     * @return  true if the resource could be acquired by this job; false otherwise.
     */
    boolean acquireLock(String lockingComponent, String resource, String flowId);

    boolean acquireLock(final String lockingComponent, final String resource, final String flowId,
            long expectedMaximumDuration);

    void releaseLock(String resource, String flowId);

    boolean peekLock(String resource);
}
