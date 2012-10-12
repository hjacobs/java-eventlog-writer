package de.zalando.zomcat.util;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import junit.framework.Assert;

/**
 * junit test for {@link LinkedBoundedQueue LinkedBoundedQueue}.
 *
 * @author  fbrick
 */
public class LinkedBoundedQueueTest {

    private static final int MAX_QUEUE_SIZE = 5;
    private static final int SLEEP_TIME_IN_MILLIS = 100;
    private static final int RUNNERS = 10;

    @Test
    public void testAdd() throws InterruptedException {
        final LinkedBoundedQueue<Integer> queue = new LinkedBoundedQueue<Integer>(MAX_QUEUE_SIZE);

        final AtomicInteger finishedRunnerCount = new AtomicInteger(0);

        for (int i = 0; i < RUNNERS; i++) {
            final LinkedBoundedQueueTestRunner runner = new LinkedBoundedQueueTestRunner(queue, finishedRunnerCount);

            final Thread t = new Thread(runner);

            t.start();
        }

        while (finishedRunnerCount.get() < RUNNERS) {
            Thread.sleep(SLEEP_TIME_IN_MILLIS);
        }

        Assert.assertEquals(MAX_QUEUE_SIZE, queue.size());
    }
}
