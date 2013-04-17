package de.zalando.zomcat.jobs.management.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.quartz.SchedulerConfigException;

import org.quartz.spi.ThreadPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO test with negative values
// TODO test wait timeout
// TODO test work queue
// TODO add set validation and remove from init method
public class QuartzThreadPoolAdapter implements ThreadPool {

    private static final Logger LOG = LoggerFactory.getLogger(QuartzThreadPoolAdapter.class);

    private final Object availableThreadsLock = new Object();

    private int corePoolSize = 25;

    private int maximumPoolSize = 50;

    // Maximum time in milliseconds that excess idle threads will wait for new tasks before terminating.
    // Default: 60 seconds
    private long keepAliveTime = 60000;

    // Shutdown timeout in milliseconds.
    // Default: 10 seconds
    private long shutdownTimeout = 10000;

    // Queue size. If the queue size is zero, thread pool will block if it reaches the maximum size
    // Default 0
    private int queueSize = 0;

    private String instanceId;

    private String instanceName;

    private ThreadPoolExecutor executor;

    public QuartzThreadPoolAdapter() { }

    /**
     * Gets the core number of threads.
     *
     * @return  the core number of threads
     */
    public int getCorePoolSize() {
        return corePoolSize;
    }

    /**
     * Sets the number of core threads in the pool - has no effect after <code>initialize()</code> has been called.
     */
    public void setCorePoolSize(final int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    /**
     * Gets the maximum allowed number of threads.
     *
     * @return  the maximum allowed number of threads
     */
    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    /**
     * Sets the number of worker threads in the pool - has no effect after <code>initialize()</code> has been called.
     */
    public void setMaximumPoolSize(final int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    /**
     * Gets the maximum time that excess idle threads will wait for new tasks before terminating.
     *
     * @return  the keep alive time
     */
    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    /**
     * Sets the maximum time that excess idle threads will wait for new tasks before terminating.
     *
     * @param  keepAliveTime
     */
    public void setKeepAliveTime(final long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    /**
     * Gets the timeout used on shutdown for running jobs.
     *
     * @return  the shutdown timeout
     */
    public long getShutdownTimeout() {
        return shutdownTimeout;
    }

    /**
     * Gets the timeout used on shutdown for running jobs.
     *
     * @param  shutdownTimeout  shtdown timeout
     */
    public void setShutdownTimeout(final long shutdownTimeout) {
        this.shutdownTimeout = shutdownTimeout;
    }

    /**
     * Gets the instance id.
     *
     * @return  the instance id
     */
    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public void setInstanceId(final String instanceId) {
        this.instanceId = instanceId;
    }

    /**
     * Gets the instance name..
     *
     * @return  the instance name
     */
    public String getInstanceName() {
        return instanceName;
    }

    @Override
    public void setInstanceName(final String instanceName) {
        this.instanceName = instanceName;
    }

    @Override
    public int getPoolSize() {
        return executor.getPoolSize();
    }

    @Override
    public void initialize() throws SchedulerConfigException {
        if (corePoolSize <= 0) {
            throw new SchedulerConfigException("Core thread count must be > 0");
        }

        if (maximumPoolSize <= 0) {
            throw new SchedulerConfigException("Maximum thread count must be > 0");
        }

        if (keepAliveTime <= 0) {
            throw new SchedulerConfigException("Thread keep alive time must be > 0");
        }

        if (maximumPoolSize < corePoolSize) {
            throw new SchedulerConfigException("Maximum thread cound must be higher than core pool size");
        }

        BlockingQueue<Runnable> workQueue = queueSize > 0 ? new LinkedBlockingQueue<Runnable>(queueSize)
                                                          : new SynchronousQueue<Runnable>();

        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
                workQueue);
    }

    @Override
    public int blockForAvailableThreads() {

        // maximum pool size is always the same
        // best effort approach
        int availableThreads = executor.getMaximumPoolSize() - executor.getActiveCount();

        if (availableThreads < 1) {

            // if we reach here we have performance problems, we need an bigger pool
            LOG.warn("Maximum quartz thread pool size reached. Please increase quartz pool size");

            synchronized (availableThreadsLock) {

                // we might have threads waiting for a long time, so lets check the number of available threads again
                availableThreads = executor.getMaximumPoolSize() - executor.getActiveCount();

                while (availableThreads < 1 && !executor.isShutdown()) {
                    try {

                        // check every half a second if there are threads available.
                        // Not the optimal solution, but if we reach the maximum number of threads we will have
                        // performance penalty anyway, because, we need to block job executions.
                        // The goal is to never reach the maximum number of threads
                        // Quartz SimpleThreadPool approach also blocks for only a small amount of time
                        // (nextRunnableLock.wait(500);)
                        availableThreadsLock.wait(500);
                    } catch (InterruptedException e) {
                        LOG.warn("Available threads wait interrupted", e);
                    }

                    availableThreads = executor.getMaximumPoolSize() - executor.getActiveCount();
                }
            }
        }

        return availableThreads;
    }

    @Override
    public boolean runInThread(final Runnable runnable) {
        boolean ran = false;

        try {
            this.executor.execute(runnable);
            ran = true;
        } catch (RejectedExecutionException e) {
            LOG.error("Could not execute job", e);
        }

        return ran;
    }

    @Override
    public void shutdown(final boolean waitForJobsToComplete) {
        executor.shutdown();

        if (waitForJobsToComplete) {
            try {
                executor.awaitTermination(shutdownTimeout, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                LOG.error("Job thread pool interrupted", e);
            }
        }
    }

}
