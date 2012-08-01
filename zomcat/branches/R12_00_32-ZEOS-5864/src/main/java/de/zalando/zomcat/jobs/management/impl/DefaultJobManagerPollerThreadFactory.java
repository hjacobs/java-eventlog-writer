package de.zalando.zomcat.jobs.management.impl;

import java.util.concurrent.ThreadFactory;

import de.zalando.zomcat.jobs.management.JobManager;

/**
 * Default Implementation of {@link JobManager} interface. Simple component that manages Quartz Jobs. Features include:
 * on demand scheduling, on demand rescheduling, on demand job cancelation, maintanence mode support, job history incl
 * results etc.
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
