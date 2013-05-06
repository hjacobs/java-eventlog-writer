package de.zalando.zomcat.jobs.management.impl;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    private int corePoolSize = 0;

    private int maximumPoolSize = 50;

    // Maximum time in milliseconds that excess idle threads will wait for new tasks before terminating.
    // Default: 5 minutes
    private long keepAliveTime = 5 * 60 * 1000;

    // Shutdown timeout in milliseconds.
    // Default: 10 minutes
    private long shutdownTimeout = 10 * 60 * 1000;

    private String instanceId;

    private String instanceName;

    private ThreadPoolExecutorHelper threadPoolHelper;

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
        return threadPoolHelper.getPoolSize();
    }

    @Override
    public void initialize() throws SchedulerConfigException {
        if (maximumPoolSize < corePoolSize) {
            throw new SchedulerConfigException("Maximum thread cound must be higher than core pool size");
        }

        ScalingQueue scalingQueue = new ScalingQueue();
        threadPoolHelper = new ThreadPoolExecutorHelper(corePoolSize, maximumPoolSize, keepAliveTime, shutdownTimeout,
                scalingQueue, scalingQueue);
    }

    @Override
    public int blockForAvailableThreads() {
        // This method is always executed by the same scheduler thread,
        // it should block until there is at least one available thread.

        return threadPoolHelper.blockForAvailableThreads();
    }

    @Override
    public boolean runInThread(final Runnable runnable) {
        // this method is executed sequentially after blockForAvailableThreads() and in the same scheduler thread

        boolean ran = false;

        if (runnable != null) {
            try {
                threadPoolHelper.execute(runnable);
                ran = true;
            } catch (RejectedExecutionException e) {
                LOG.error("Could not execute job", e);
            }
        }

        return ran;
    }

    @Override
    public void shutdown(final boolean waitForJobsToComplete) {
        threadPoolHelper.shutdown(waitForJobsToComplete);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("QuartzThreadPoolExecutorAdapter [corePoolSize=");
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
        builder.append(", threadPoolHelper=");
        builder.append(threadPoolHelper);
        builder.append("]");

        return builder.toString();
    }

    private static final class ThreadPoolExecutorHelper extends ThreadPoolExecutor {

        private static final Logger LOG = LoggerFactory.getLogger(ThreadPoolExecutorHelper.class);

        private final Lock lock = new ReentrantLock();
        private final Condition notFull = lock.newCondition();

        private int count = 0;

        // cache maximumPoolSize to minimize locking on thread pool executor
        // ThreadPoolExecutor.getPoolSize should return what we want, but it acquires one lock to do it
        // Since we can't change maximumPoolSize dynamically we can safely cache this value
        private final int maximumPoolSize;
        private final long shutdownTimeout;

        private ThreadPoolExecutorHelper(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime,
                final long shutdownTimeout, final BlockingQueue<Runnable> workQueue,
                final RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, workQueue);
            setRejectedExecutionHandler(handler);
            this.maximumPoolSize = maximumPoolSize;
            this.shutdownTimeout = shutdownTimeout;
        }

        @Override
        public void execute(final Runnable command) {

            // increase the number of working threads
            lock.lock();
            try {
                ++count;
            } finally {
                lock.unlock();
            }

            super.execute(command);
        }

        @Override
        protected void afterExecute(final Runnable r, final Throwable t) {

            // This method is invoked by the thread that executed the task.
            super.afterExecute(r, t);

            lock.lock();
            try {
                --count;
                notFull.signal();
            } finally {
                lock.unlock();
            }
        }

        public int blockForAvailableThreads() {
            int availableThreads;

            lock.lock();
            try {
                availableThreads = maximumPoolSize - count;
                if (availableThreads < 1) {

                    // We have performance problems. The pool is too short and some tasks are waiting for
                    // available resources. We should notify this problem!
                    LOG.warn("Maximum quartz thread pool size reached: {}. Please increase quartz pool size",
                        maximumPoolSize);

                    try {
                        while (availableThreads < 1 && !isShutdown()) {
                            notFull.await();
                            availableThreads = maximumPoolSize - count;
                        }
                    } catch (InterruptedException e) {

                        // skip await if InterruptedException is thrown
                        LOG.warn("Available threads wait interrupted", e);
                    }
                }
            } finally {
                lock.unlock();
            }

            return availableThreads;
        }

        public void shutdown(final boolean waitForJobsToComplete) {
            LOG.info("Shutting down quartz thread pool");

            super.shutdown();

            lock.lock();
            try {
                notFull.signalAll();
            } finally {
                lock.unlock();
            }

            if (waitForJobsToComplete) {
                LOG.info("Waiting {} ms for jobs termination", shutdownTimeout);
                try {
                    if (!super.awaitTermination(shutdownTimeout, TimeUnit.MILLISECONDS)) {
                        LOG.error(
                            "Quartz thread pool timeout ({} ms) elapsed before termination. Some jobs might have terminated abruptly",
                            shutdownTimeout);
                    }
                } catch (InterruptedException e) {
                    LOG.error("Job thread pool interrupted", e);
                }
            }

            LOG.info("Shutdown complete: {}", this);
        }
    }

    /**
     * Force the thread pool executor to create new threads instead of queuing tasks. Only queue tasks if the task is
     * rejected.
     *
     * @author  pribeiro
     */
    private static final class ScalingQueue extends LinkedBlockingQueue<Runnable> implements RejectedExecutionHandler {

        private static final Logger LOG = LoggerFactory.getLogger(ScalingQueue.class);

        private static final long serialVersionUID = -6338917192992076718L;

        @Override
        public boolean offer(final Runnable e) {

            // force the thread pool to grow and only add tasks thought the rejected execution handler.
            // According to the ThreadPoolExecutor, ff there are more than corePoolSize but less than maximumPoolSize
            // threads running, a new thread will be created only if the queue is full
            return false;
        }

        @Override
        public boolean offer(final Runnable e, final long timeout, final TimeUnit unit) throws InterruptedException {

            // force the thread pool to grow and only add tasks thought the rejected execution handler
            // According to the ThreadPoolExecutor, ff there are more than corePoolSize but less than maximumPoolSize
            // threads running, a new thread will be created only if the queue is full
            return false;
        }

        @Override
        public boolean add(final Runnable e) {

            // force the thread pool to grow and only add tasks thought the rejected execution handler
            // According to the ThreadPoolExecutor, ff there are more than corePoolSize but less than maximumPoolSize
            // threads running, a new thread will be created only if the queue is full
            return false;
        }

        @Override
        public boolean addAll(final Collection<? extends Runnable> c) {

            // force the thread pool to grow and only add tasks thought the rejected execution handler
            // According to the ThreadPoolExecutor, ff there are more than corePoolSize but less than maximumPoolSize
            // threads running, a new thread will be created only if the queue is full
            return false;
        }

        @Override
        public void put(final Runnable e) throws InterruptedException {
            throw new UnsupportedOperationException("Operation not supported");
        }

        @Override
        public void rejectedExecution(final Runnable r, final ThreadPoolExecutor executor) {

            // This shouldn't happen very often.
            LOG.debug("Task {} rejected.", r);

            // if the task was rejected due to a shutdown, just thrown a RejectedExecutionException, we can't do
            // anything about that
            if (executor.isShutdown()) {
                throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + executor.toString()
                        + "because thread pool is being shutdown");
            }

            // method afterExecute() is invoked by the thread that executed the task, in other words, we are
            // decrementing the number of worker threads but the thread is still unavailable on the thread pool.
            // If the pool if full (poolSize == maximumPoolSize) and if one task starts running after method
            // afterExecute() (this method notifies waiting threads), but before the thread returns to the pool, the
            // task will be rejected. In this scenario, because we know that the thread will be
            // available on the pool in a few ms, we should add the task to the queue and delay
            // the execution.
            if (!super.offer(r)) {

                // seems that the queue is full. Reject the task
                throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + executor.toString());
            }
        }
    }
}
