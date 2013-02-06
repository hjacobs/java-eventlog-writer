package de.zalando.zomcat.jobs.batch.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple immutable batch execution thread factory.
 *
 * @author  pribeiro
 */
public final class BatchExecutionThreadFactory implements ThreadFactory {

    private static final Logger LOG = LoggerFactory.getLogger(BatchExecutionThreadFactory.class);

    private final String threadNamePrefix;

    private final boolean daemon;

    private final int threadPriority;

    private final AtomicInteger counter;

    /**
     * Instantiates a new factory which creates user threads (user threads prevents the JVM to exit) with normal
     * priority and with the specified <code>threadNamePrefix.</code>.
     *
     * @param  threadNamePrefix  thread name prefix
     */
    public BatchExecutionThreadFactory(final String threadNamePrefix) {
        this(threadNamePrefix, false, Thread.NORM_PRIORITY);
    }

    /**
     * Instantiates a new factory with specified properties.
     *
     * @param  threadNamePrefix  thread name prefix
     * @param  daemon            if true, marks each thread as a daemon (won't prevent the JVM to exit)
     * @param  threadPriority    thread priority
     */
    public BatchExecutionThreadFactory(final String threadNamePrefix, final boolean daemon, final int threadPriority) {
        this.threadNamePrefix = threadNamePrefix;
        this.daemon = daemon;
        this.threadPriority = threadPriority;
        this.counter = new AtomicInteger(0);
    }

    @Override
    public Thread newThread(final Runnable r) {
        String threadName = threadNamePrefix + "-" + counter.incrementAndGet();

        LOG.debug("Creating a new thread for batch processing, with name: {}", threadName);

        Thread t = new Thread(r, threadName);
        t.setDaemon(daemon);
        t.setPriority(threadPriority);

        return t;
    }
}
