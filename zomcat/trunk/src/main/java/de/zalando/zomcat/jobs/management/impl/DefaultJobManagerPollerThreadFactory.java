package de.zalando.zomcat.jobs.management.impl;

import java.util.concurrent.ThreadFactory;

/**
 * ThreadFactory for JobConfigurationPoller Executor.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public final class DefaultJobManagerPollerThreadFactory implements ThreadFactory {

    private static final transient String THREAD_NAME = "JobManagerConfigUpdater-1";

    @Override
    public Thread newThread(final Runnable arg0) {
        final Thread retVal = new Thread(arg0);
        retVal.setName(THREAD_NAME);
        return retVal;
    }

}
