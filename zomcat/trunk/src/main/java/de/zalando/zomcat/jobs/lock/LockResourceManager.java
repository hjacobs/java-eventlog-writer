package de.zalando.zomcat.jobs.lock;

public interface LockResourceManager {

    /**
     * @param   resource
     *
     * @return  true if the resource could be acquired by this job; false otherwise.
     */
    boolean acquireLock(String resource);

    boolean acquireLock(String resource, long expectedMaximumDuration);

    void releaseLock(String resource);
}
