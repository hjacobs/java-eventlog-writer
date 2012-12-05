package de.zalando.zomcat.jobs.batch.transition.strategy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.zalando.utils.Pair;

import de.zalando.zomcat.flowid.FlowId;
import de.zalando.zomcat.jobs.batch.transition.BatchExecutionStrategy;
import de.zalando.zomcat.jobs.batch.transition.JobResponse;
import de.zalando.zomcat.jobs.batch.transition.WriteTime;

/**
 * Executes each chunk in a thread. This takes dynamically the amount of chunks and starts a thread pool with the same
 * size. Subclasses are free to define more specific parallel strategies. Concrete implementations must define
 * makeChunk.
 *
 * @param   <ITEM_TYPE>
 *
 * @author  john
 */
public abstract class ParallelChunkBulkProcessingExecutionStrategy<ITEM_TYPE>
    extends BatchExecutionStrategy<ITEM_TYPE> {

    private final Map<String, Future<Pair<List<ITEM_TYPE>, List<JobResponse<ITEM_TYPE>>>>> resultMap = Maps
            .newHashMap();

    private static final Logger LOG = LoggerFactory.getLogger(ParallelChunkBulkProcessingExecutionStrategy.class);

    private ExecutorService threadPool;

    @Override
    protected void setupExecution(final Map<String, Collection<ITEM_TYPE>> chunks) {

        final int numThreads = chunks.keySet().size();

        LOG.info("Creating executor pool with {} threads.", numThreads);

        threadPool = Executors.newFixedThreadPool(numThreads);
    }

    @Override
    protected void cleanup(final Map<String, Collection<ITEM_TYPE>> chunks) {
        threadPool.shutdown();
    }

    @Override
    public void processChunk(final String chunkId, final Collection<ITEM_TYPE> items) throws Exception {

        // clone the flowid:
        final Stack<?> cloneStack = FlowId.cloneStack();

        final Future<Pair<List<ITEM_TYPE>, List<JobResponse<ITEM_TYPE>>>> f = threadPool.submit(
                new Callable<Pair<List<ITEM_TYPE>, List<JobResponse<ITEM_TYPE>>>>() {

                    @Override
                    public Pair<List<ITEM_TYPE>, List<JobResponse<ITEM_TYPE>>> call() {

                        // inherit the existing flow for this thread:
                        FlowId.inherit(cloneStack);
                        try {

                            final List<ITEM_TYPE> successfulItems = Lists.newArrayList();
                            final List<JobResponse<ITEM_TYPE>> failedItems = Lists.newArrayList();

                            LOG.debug("Starting execution of chunk id {} on [{}] ({} items)",
                                new Object[] {chunkId, Thread.currentThread().getName(), items.size()});

                            for (final ITEM_TYPE item : items) {

                                LOG.debug("Dispatching item {} to processor {}.", item,
                                    Thread.currentThread().getName());

                                try {

                                    processor.process(item);
                                    successfulItems.add(item);
                                } catch (final Throwable t) {
                                    final JobResponse<ITEM_TYPE> response = new JobResponse<ITEM_TYPE>(item);
                                    response.addErrorMessage(Throwables.getStackTraceAsString(t));
                                    failedItems.add(response);
                                }

                                if (writeTime == WriteTime.AT_EACH_ITEM) {
                                    write(successfulItems, failedItems);
                                    successfulItems.clear();
                                    failedItems.clear();
                                }
                            }

                            if (writeTime == WriteTime.AT_EACH_CHUNK) {
                                write(successfulItems, failedItems);
                                successfulItems.clear();
                                failedItems.clear();
                            }

                            return Pair.of(successfulItems, failedItems);
                        } finally {
                            FlowId.clear();
                        }
                    }

                });
        resultMap.put(chunkId, f);
    }

    @Override
    public int getProcessedCount() {

        int processedCount = 0;

        final Set<String> keySet = resultMap.keySet();
        for (final String k : keySet) {

            final Future<Pair<List<ITEM_TYPE>, List<JobResponse<ITEM_TYPE>>>> future = resultMap.get(k);

            try {
                processedCount += (future.get().getFirst().size() + future.get().getSecond().size());
            } catch (final Exception e) {
                throw new RuntimeException("exception.", e);
            }
        }

        return processedCount;
    }

    @Override
    protected Pair<List<ITEM_TYPE>, List<JobResponse<ITEM_TYPE>>> joinResults() {

        final List<ITEM_TYPE> successes = Lists.newArrayList();
        final List<JobResponse<ITEM_TYPE>> failures = Lists.newArrayList();

        final Set<String> keySet = resultMap.keySet();

        for (final String k : keySet) {

            final Future<Pair<List<ITEM_TYPE>, List<JobResponse<ITEM_TYPE>>>> future = resultMap.get(k);

            int failedFutures = 0;

            try {

                // processedCount += (future.get().getFirst().size() + future.get().getSecond().size());
                final Pair<List<ITEM_TYPE>, List<JobResponse<ITEM_TYPE>>> futureResult = future.get();
                successes.addAll(futureResult.getFirst());
                failures.addAll(futureResult.getSecond());
            } catch (final Exception ex) {
                LOG.debug("Failed to get execution result", ex);
                failedFutures++;
            }

            if (failedFutures > 0) {
                LOG.warn("Failed to gather execution results from {} tasks.", failedFutures);
            }
        }

        return Pair.of(successes, failures);
    }
}
