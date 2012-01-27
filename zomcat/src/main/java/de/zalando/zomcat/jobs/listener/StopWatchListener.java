package de.zalando.zomcat.jobs.listener;

import java.lang.management.ThreadMXBean;

import org.apache.log4j.Logger;

import org.joda.time.DateTime;

import org.quartz.JobExecutionContext;

import de.zalando.zomcat.jobs.JobListener;
import de.zalando.zomcat.jobs.RunningWorker;

public class StopWatchListener implements JobListener {
    private static final Logger LOG = Logger.getLogger(StopWatchListener.class);

    private long threadStartNanoSeconds = -1;
    private long timeStart = -1;
    private long threadStopNanoSeconds = -1;
    private long timeStop = -1;
    final ThreadMXBean tmxb = null; // TODO: to be tested if there is enough time: ManagementFactory.getThreadMXBean();

    @Override
    public void startRunning(final RunningWorker runningWorker, final JobExecutionContext context,
            final String appInstanceKey) {
        if (tmxb != null && tmxb.isCurrentThreadCpuTimeSupported() && tmxb.isThreadCpuTimeEnabled()) {
            threadStartNanoSeconds = tmxb.getCurrentThreadCpuTime();
        }

        threadStopNanoSeconds = -1;
        timeStop = -1;
        timeStart = System.currentTimeMillis();
    }

    @Override
    public void stopRunning(final RunningWorker runningWorker, final Throwable t) {
        if (tmxb != null && tmxb.isCurrentThreadCpuTimeSupported() && tmxb.isThreadCpuTimeEnabled()) {
            threadStopNanoSeconds = tmxb.getCurrentThreadCpuTime();
        }

        timeStop = System.currentTimeMillis();
        LOG.debug("Job finished. thread CPU usage: " + getThreadCPUNanoSeconds() + ", thread duration: "
                + getThreadDuration());
    }

    public boolean isRunning() {
        return timeStop == -1;
    }

    public long getThreadCPUNanoSeconds() {
        return threadStopNanoSeconds == -1 ? -1 : (threadStopNanoSeconds - threadStartNanoSeconds);
    }

    public long getThreadDuration() {
        return isRunning() == true ? -1 : (timeStart - timeStop);
    }

    public DateTime getStartTime() {
        return new DateTime(timeStart);
    }
}
