package de.zalando.zomcat.util;

import java.lang.reflect.Field;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;

import org.quartz.JobExecutionContext;

import org.quartz.core.JobRunShell;

/**
 * this execution handler does nothing when a taskExecuter rejected a job. It will only log a warning into logfile. This
 * way it is simply discarded without throwing an exception (and thereby stopping the queue FOREVER).
 *
 * @author  fbrick
 * @see     DiscardingThreadPoolTaskExecutor for more details.
 */
public class DiscardingRejectedExecutionHandler implements RejectedExecutionHandler {

    private static final Logger LOG = Logger.getLogger(DiscardingRejectedExecutionHandler.class);

    public DiscardingRejectedExecutionHandler() {
        super();
    }

    /**
     * Does nothing, which has the effect of discarding task r.
     *
     * @param  r  the runnable task requested to be executed
     * @param  e  the executor attempting to execute this task
     */
    @Override
    public void rejectedExecution(final Runnable runnable, final ThreadPoolExecutor threadPoolExecutor) {
        String jobName = runnable.getClass().getSimpleName();
        if (runnable instanceof JobRunShell) {
            final JobRunShell jobRunShell = (JobRunShell) runnable;
            try {
                final Field jecField = jobRunShell.getClass().getDeclaredField("jec");
                if (jecField != null) {
                    jecField.setAccessible(true);

                    final JobExecutionContext jec = (JobExecutionContext) jecField.get(jobRunShell);
                    if ((jec != null) && (jec.getJobDetail() != null)) {
                        jobName = jec.getJobDetail().getName();
                    }
                }
            } catch (NoSuchFieldException ex) {
                LOG.info("Could not retrieve JobExecutionContext from JobRunShell", ex);
            } catch (SecurityException ex) {
                LOG.info("Could not retrieve JobExecutionContext from JobRunShell", ex);
            } catch (IllegalArgumentException ex) {
                LOG.info("Could not retrieve JobExecutionContext from JobRunShell", ex);
            } catch (IllegalAccessException ex) {
                LOG.info("Could not retrieve JobExecutionContext from JobRunShell", ex);
            }
        }

        if (threadPoolExecutor.getPoolSize() >= threadPoolExecutor.getMaximumPoolSize()) {
            LOG.warn("Rejected execution for Job '" + jobName + ", queue is full: thread pool size = "
                    + threadPoolExecutor.getPoolSize() + " (maximum: " + threadPoolExecutor.getMaximumPoolSize() + ')');
        } else {
            LOG.warn("Rejected execution because of unknown reasons for job '" + jobName + "', threadPoolExecutor = "
                    + threadPoolExecutor);
        }
    }
}
