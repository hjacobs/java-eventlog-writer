package de.zalando.zomcat.jobs.management.impl;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.quartz.SchedulerConfigException;

import org.quartz.spi.ThreadPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * Simple implementation of quartz thread pool based on {@link ThreadPoolExecutor}.
 *
 * <p>This pool is capable of growing and shrink on demand preserving resources.
 *
 * @author  pribeiro
 */
public class QuartzThreadPoolExecutorAdapter implements ThreadPool {

    private static final Logger LOG = LoggerFactory.getLogger(QuartzThreadPoolExecutorAdapter.class);

    private final Object availableThreadsLock = new Object();

    private int corePoolSize = 1;

    private int maximumPoolSize = 50;

    // Maximum time in milliseconds that excess idle threads will wait for new tasks before terminating.
    // Default: 5 minutes
    private long keepAliveTime = 5 * 60 * 1000;

    // Shutdown timeout in milliseconds.
    // Default: 10 minutes
    private long shutdownTimeout = 10 * 60 * 1000;

    private String instanceId;

    private String instanceName;

    private ThreadPoolExecutorHelper helper;

    public QuartzThreadPoolExecutorAdapter() { }

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
     *
     * @param  corePoolSize  corePoolSize the core size
     */
    public void setCorePoolSize(final int corePoolSize) {
        Preconditions.checkArgument(corePoolSize >= 0, "Core thread count must be >= 0");
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
     *
     * @param  maximumPoolSize  maximum pool size
     */
    public void setMaximumPoolSize(final int maximumPoolSize) {
        Preconditions.checkArgument(maximumPoolSize > 0, "Maximum thread count must be > 0");
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
     * Sets the maximum time that excess idle threads will wait for new tasks before terminating - has no effect after
     * <code>initialize()</code> has been called.
     *
     * @param  keepAliveTime
     */
    public void setKeepAliveTime(final long keepAliveTime) {
        Preconditions.checkArgument(keepAliveTime >= 0, "Thread keep alive time must be >= 0");
        this.keepAliveTime = keepAliveTime;
    }

    /**
     * Gets the timeout used on shutdown for running jobs - has no effect after <code>initialize()</code> has been
     * called.
     *
     * @return  the shutdown timeout
     */
    public long getShutdownTimeout() {
        return shutdownTimeout;
    }

    /**
     * Gets the timeout used on shutdown for running jobs - has no effect after <code>initialize()</code> has been
     * called.
     *
     * @param  shutdownTimeout  shutdown timeout
     */
    public void setShutdownTimeout(final long shutdownTimeout) {
        Preconditions.checkArgument(shutdownTimeout >= 0, "Shutdown timeout must be >= 0");
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
        return helper.getPoolSize();
    }

    @Override
    public void initialize() throws SchedulerConfigException {
        if (maximumPoolSize < corePoolSize) {
            throw new SchedulerConfigException("Maximum thread cound must be higher than core pool size");
        }

        helper = new ThreadPoolExecutorHelper(corePoolSize, maximumPoolSize, keepAliveTime, shutdownTimeout);
    }

    @Override
    public int blockForAvailableThreads() {
        // this method is always executed by the same scheduler thread

        return helper.blockForAvailableThreads();
    }

    @Override
    public boolean runInThread(final Runnable runnable) {
        // this method is executed sequentially after blockForAvailableThreads() and in the same scheduler thread

        boolean ran = false;

        if (runnable != null) {
            try {
                helper.execute(runnable);
                ran = true;
            } catch (RejectedExecutionException e) {
                LOG.error("Could not execute job", e);
            }
        }

        return ran;
    }

    @Override
    public void shutdown(final boolean waitForJobsToComplete) {
        helper.shutdown(waitForJobsToComplete);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("QuartzThreadPoolExecutorAdapter [availableThreadsLock=");
        builder.append(availableThreadsLock);
        builder.append(", corePoolSize=");
        builder.append(corePoolSize);
        builder.append(", maximumPoolSize=");
        builder.append(maximumPoolSize);
        builder.append(", keepAliveTime=");
        builder.append(keepAliveTime);
        builder.append(", shutdownTimeout=");
        builder.append(shutdownTimeout);
        builder.append(", instanceId=");
        builder.append(instanceId);
        builder.append(", instanceName=");
        builder.append(instanceName);
        builder.append(", executor=");
        builder.append(helper);
        builder.append("]");

        return builder.toString();
    }

    private static final class ThreadPoolExecutorHelper extends ThreadPoolExecutor {

        private final Object lock = new Object();

        private int count = 0;

        // cache maximumPoolSize to minimize locking on thread pool executor
        // ThreadPoolExecutor.getPoolSize should return what we want, but it acquires one lock to do it
        // Since we can't change maximumPoolSize dynamically we can safely cache this value
        private final int maximumPoolSize;
        private final long shutdownTimeout;

        public ThreadPoolExecutorHelper(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime,
                final long shutdownTimeout) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,

                // use a linked blocking queue because method afterExecute()  is invoked by the thread that executed
                // the task
                new LinkedBlockingQueue<Runnable>());

            this.maximumPoolSize = maximumPoolSize;
            this.shutdownTimeout = shutdownTimeout;
        }

        @Override
        public void execute(final Runnable command) {

            // increase the number of threads
            synchronized (lock) {
                count++;
            }

            super.execute(command);
        }

        public int blockForAvailableThreads() {
            int availableThreads;

            synchronized (lock) {
                availableThreads = maximumPoolSize - count;

                if (availableThreads < 1) {
                    // if we are here, we have performance problems. The pool is to short and some tasks are waiting for
                    // available resources. We should notify this problem

                    // only log once
                    LOG.warn("Maximum quartz thread pool size reached. Please increase quartz pool size");

                    while (availableThreads < 1 && !isShutdown()) {

                        try {

                            // prevent this to lock forever, keep trying
                            lock.wait(1000);
                        } catch (InterruptedException e) {
                            LOG.warn("Available threads wait interrupted", e);
                        }

                        availableThreads = maximumPoolSize - count;
                    }
                }
            }

            return availableThreads;
        }

        public void shutdown(final boolean waitForJobsToComplete) {
            LOG.info("Shutting down quartz thread pool");

            super.shutdown();

            // notify waiting threads
            synchronized (lock) {
                if (count == 0) {
                    lock.notifyAll();
                }
            }

            if (waitForJobsToComplete) {
                LOG.info("Waiting {} ms for jobs termination", shutdownTimeout);
                try {
                    if (!super.awaitTermination(shutdownTimeout, TimeUnit.MILLISECONDS)) {
                        LOG.error(
                            "Quartz thread pool quartz timeout elapsed before termination. Some jobs might have terminated abruptly");
                    }
                } catch (InterruptedException e) {
                    LOG.error("Job thread pool interrupted", e);
                }
            }

            LOG.info("Shutdown complete: {}", super.toString());
        }

        @Override
        protected void afterExecute(final Runnable r, final Throwable t) {
            super.afterExecute(r, t);

            // The main reason, why we are using LinkedBlockingQueue instead of a SynchronousQueue.
            // We are decreasing the number of running threads inside the thread that executed the task, so this
            // worker is not yet available.
            // If method blockForAvailableThreads() returns immediately we should block the task for a
            // small amount of time and not reject it.

            synchronized (lock) {
                count--;
                if (count == 0) {
                    lock.notifyAll();
                }
            }
        }
    }
}
