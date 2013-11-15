package de.zalando.zomcat.util;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;

/**
 * junit test for {@link LinkedBoundedQueue LinkedBoundedQueue}.
 *
 * @author  fbrick
 */
public class LinkedBoundedQueueTest {

    @Test
    public void testMultipleWriters() throws Exception {

        final int capacity = 2;

        final LinkedBoundedQueue<Integer> queue = new LinkedBoundedQueue<>(capacity);

        final AtomicBoolean gate = new AtomicBoolean(true);

        int numberReaders = 50;
        ExecutorService readers = Executors.newFixedThreadPool(numberReaders);

        try {
            Runnable readerCommand = new Runnable() {

                @Override
                public void run() {
                    while (gate.get()) {
                        int listSize = queue.view().size();
                        Assert.assertTrue(String.valueOf(listSize), listSize <= capacity);
                    }
                }
            };

            for (int i = 0; i < numberReaders; i++) {
                readers.execute(readerCommand);
            }

            int numberWriters = 175;
            ExecutorService writers = Executors.newFixedThreadPool(numberWriters);

            try {
                Runnable writterCommand = new Runnable() {

                    @Override
                    public void run() {
                        queue.add(new Random().nextInt());
                    }
                };

                final int iterations = 100000;
                for (int i = 0; i < iterations; i++) {
                    writers.execute(writterCommand);
                }
            } finally {
                writers.shutdown();
                writers.awaitTermination(30, TimeUnit.SECONDS);
            }

            gate.set(false);
        } finally {
            readers.shutdown();
            readers.awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    @Test
    public void testViewOrder() {
        final int capacity = 2;
        final LinkedBoundedQueue<Integer> queue = new LinkedBoundedQueue<>(capacity);

        List<Integer> view = queue.view();
        Assert.assertEquals(0, view.size());

        queue.add(1);
        view = queue.view();
        Assert.assertEquals(1, view.size());
        Assert.assertEquals(1, view.get(0).intValue());

        queue.add(2);
        view = queue.view();
        Assert.assertEquals(2, view.size());
        Assert.assertEquals(2, view.get(0).intValue());
        Assert.assertEquals(1, view.get(1).intValue());

        queue.add(3);
        view = queue.view();
        Assert.assertEquals(2, view.size());
        Assert.assertEquals(3, view.get(0).intValue());
        Assert.assertEquals(2, view.get(1).intValue());
    }
}
