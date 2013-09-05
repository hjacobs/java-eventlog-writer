package de.zalando.zomcat.jobs.lock;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;

@SProcService(namespace = "zz_commons.job_resource")
public interface LockResourceSprocService extends LockResourceManager {

    @Override
    @SProcCall(timeoutInMilliSeconds = 10000)
    boolean acquireLock(@SProcParam String lockingComponent, @SProcParam String resource, @SProcParam String flowid,
            @SProcParam long expectedMaximumDuration);

    @Override
    @SProcCall(timeoutInMilliSeconds = 10000)
    void releaseLock(@SProcParam String resource, @SProcParam String flowid);

    @Override
    @SProcCall(timeoutInMilliSeconds = 10000)
    boolean peekLock(@SProcParam String resource);

}
