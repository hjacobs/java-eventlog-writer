package de.zalando.zomcat.jobs.listener;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.joda.time.DateTime;

import org.quartz.JobExecutionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.zomcat.jobs.JobListener;
import de.zalando.zomcat.jobs.RunningWorker;

public class StopWatchListener implements JobListener {
    private static final Logger LOG = LoggerFactory.getLogger(StopWatchListener.class);

    private long threadStartNanoSeconds = -1;
    private long timeStart = -1;
    private long threadStopNanoSeconds = -1;
    private long timeStop = -1;
    final ThreadMXBean tmxb = ManagementFactory.getThreadMXBean();

    @Override
    public void startRunning(final RunningWorker runningWorker, final JobExecutionContext context,
            final String appInstanceKey) {
        threadStopNanoSeconds = -1;

        if (tmxb != null && tmxb.isCurrentThreadCpuTimeSupported() && tmxb.isThreadCpuTimeEnabled()) {
            threadStartNanoSeconds = tmxb.getCurrentThreadCpuTime();
        }

        timeStop = -1;
        timeStart = System.currentTimeMillis();
    }

    @Override
    public void stopRunning(final RunningWorker runningWorker, final Throwable t) {
        if (tmxb != null && tmxb.isCurrentThreadCpuTimeSupported() && tmxb.isThreadCpuTimeEnabled()) {
            threadStopNanoSeconds = tmxb.getCurrentThreadCpuTime();
        }

        timeStop = System.currentTimeMillis();

        // TODO: add "getThreadCPUNanoSeconds" again (as soon as it's working)
        LOG.debug("Job finished in {} ms", getThreadDuration());
    }

    public boolean isRunning() {
        return timeStop == -1;
    }

    public long getThreadCPUNanoSeconds() {
        return threadStopNanoSeconds == -1 ? -1 : (threadStopNanoSeconds - threadStartNanoSeconds);
    }

    public long getThreadDuration() {
        return isRunning() ? -1 : (timeStop - timeStart);
    }

    public DateTime getStartTime() {
        return new DateTime(timeStart);
    }
}
