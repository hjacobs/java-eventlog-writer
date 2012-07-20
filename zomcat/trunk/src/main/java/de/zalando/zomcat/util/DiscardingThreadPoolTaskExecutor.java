package de.zalando.zomcat.util;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Spring's original <code>ThreadPoolTaskExecutor</code> has the following problem when used with jobs which extends
 * Spring's <code>Job</code>: If the queue is full because the tasks have too big running duration and the queue gets
 * more jobs in then out then the task executor will throw an exception, works all remaining queue items but then QUITS
 * forever until restart of tomcat.<br/>
 * We have different task executors for different tasks, e.g. processing the mail queue. So all jobs are the same for
 * one task executor. It is not important which job will be executed. But the task executor is NOT ALLOWED TO TERMINATE.
 * So this task executor will drop the new incoming message if the queue is already full, logs a warning but will not
 * terminate! Then IT-team has to look how to improve performance of the task which makes problems.
 *
 * @author  fbrick
 */
public class DiscardingThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    private static final long serialVersionUID = -4959109909128801854L;

    public DiscardingThreadPoolTaskExecutor() {
        super();

        setRejectedExecutionHandler(new DiscardingRejectedExecutionHandler());
    }

}
