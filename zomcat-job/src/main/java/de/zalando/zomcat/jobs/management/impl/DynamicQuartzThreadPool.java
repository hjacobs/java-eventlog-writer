package de.zalando.zomcat.jobs.management.impl;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.quartz.SchedulerConfigException;

import org.quartz.spi.ThreadPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Thread Pool - acting dynamically - new Thread per Task - max concurrent Thread Count is 50. Contains custom
 * Thread Class allowing interaction with ThreadPool itself. Threads are not being reused by this ThreadPool but are
 * created on demand.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public class DynamicQuartzThreadPool implements ThreadPool {

    private static final int MAX_RUNNING_WORKER_COUNT = 50;

    private static final Logger LOG = LoggerFactory.getLogger(DynamicQuartzThreadPool.class);

    private String instanceId;

    private String instanceName;

    /**
     * Private Thread Wrapper allowing interaction with parent class instances.
     *
     * @author  Thomas Zirke (thomas.zirke@zalando.de)
     */
    private class CustomQuartzThread extends Thread implements Runnable {

        private final Runnable runnable;

        /**
         * Constructor.
         *
         * @param  actualRunnable  The Actual Runnable to run within a Thread
         */
        public CustomQuartzThread(final Runnable actualRunnable) {
            this.runnable = actualRunnable;
        }

        @Override
        public void run() {
            try {
                runnable.run();
            } finally {
                runningWorkers.remove(this);
            }
        }
    }

    /**
     * Set of Running Workers in ThreadPool.
     */
    private Set<CustomQuartzThread> runningWorkers;

    @Override
    public boolean runInThread(final Runnable runnable) {
        if (runningWorkers.size() < MAX_RUNNING_WORKER_COUNT) {
            final CustomQuartzThread customQuartzThread = new CustomQuartzThread(runnable);
            runningWorkers.add(customQuartzThread);
            customQuartzThread.start();
            return true;
        } else if (runningWorkers.size() >= MAX_RUNNING_WORKER_COUNT) {
            LOG.warn("ThreadPool has reached Max Running Worker Count: [{}]", MAX_RUNNING_WORKER_COUNT);
        } else {
            LOG.warn("ThreadPool has been shutdown - cannot execute Runnable in ThreadPool");
        }

        return false;
    }

    @Override
    public int blockForAvailableThreads() {
        return MAX_RUNNING_WORKER_COUNT - runningWorkers.size();
    }

    @Override
    public void initialize() throws SchedulerConfigException {
        runningWorkers = new CopyOnWriteArraySet<DynamicQuartzThreadPool.CustomQuartzThread>();
    }

    @Override
    public void shutdown(final boolean waitForJobsToComplete) {
        if (waitForJobsToComplete) {
            try {
                while (runningWorkers.size() > 0) {
                    Thread.sleep(500);
                }
            } catch (final InterruptedException e) {
                LOG.error("Interrupted while waiting for WorkerThreads to finish");
            }
        }

        runningWorkers.clear();
    }

    @Override
    public int getPoolSize() {
        return runningWorkers.size();
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    @Override
    public void setInstanceId(final String schedInstId) {
        this.instanceId = schedInstId;
    }

    @Override
    public void setInstanceName(final String schedName) {
        this.instanceName = schedName;
    }

}
