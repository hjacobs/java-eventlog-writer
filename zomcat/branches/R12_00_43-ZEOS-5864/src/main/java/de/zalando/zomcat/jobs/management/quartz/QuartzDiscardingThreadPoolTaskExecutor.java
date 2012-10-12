package de.zalando.zomcat.jobs.management.quartz;

import org.springframework.scheduling.quartz.LocalTaskExecutorThreadPool;

/**
 * Extension of the Spring/Quartz used to get rid of needless warnings {@link LocalTaskExecutorThreadPool}.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public class QuartzDiscardingThreadPoolTaskExecutor extends LocalTaskExecutorThreadPool {
    @Override
    public void setInstanceId(final String schedInstId) {
        // This method exists only to get rid of warnings in Application Log - they are just annoying
    }

    @Override
    public void setInstanceName(final String schedName) {
        // This method exists only to get rid of warnings in Application Log - they are just annoying
    }
}
