package de.zalando.zomcat.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Runnable for testing {@link LinkedBoundedQueue LinkedBoundedQueue}.
 *
 * @author  fbrick
 */
public class LinkedBoundedQueueTestRunner implements Runnable {

    private static final int NUMBER_OF_ELEMENTS_TO_ADD = 10000;

    private LinkedBoundedQueue<Integer> queue = null;

    private AtomicInteger finishedRunnerCount = null;

    public LinkedBoundedQueueTestRunner(final LinkedBoundedQueue<Integer> queue,
            final AtomicInteger finishedRunnerCount) {
        super();

        this.queue = queue;
        this.finishedRunnerCount = finishedRunnerCount;
    }

    /**
     * @see  java.lang.Runnable#run()
     */
    @Override
    public void run() {
        for (int i = 0; i < NUMBER_OF_ELEMENTS_TO_ADD; i++) {
            queue.add(i);
        }

        finishedRunnerCount.addAndGet(1);
    }
}
