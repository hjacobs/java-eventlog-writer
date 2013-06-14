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
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.zalando.utils.Pair;

import de.zalando.zomcat.flowid.FlowId;
import de.zalando.zomcat.flowid.FlowUserContext;
import de.zalando.zomcat.jobs.batch.transition.BatchExecutionStrategy;
import de.zalando.zomcat.jobs.batch.transition.JobResponse;
import de.zalando.zomcat.jobs.batch.transition.WriteTime;
import de.zalando.zomcat.jobs.batch.utils.BatchExecutionThreadFactory;

/**
 * Executes each chunk in a thread. This takes dynamically the amount of chunks and starts a thread pool with the same
 * size. Subclasses are free to define more specific parallel strategies. Concrete implementations must define
 * makeChunk.
 *
 * <p>This class is NOT thread safe.
 *
 * @param   <ITEM_TYPE>
 *
 * @author  john
 */
public abstract class ParallelChunkBulkProcessingExecutionStrategy<ITEM_TYPE>
    extends BatchExecutionStrategy<ITEM_TYPE> {

    private static final Logger LOG = LoggerFactory.getLogger(ParallelChunkBulkProcessingExecutionStrategy.class);

    private Map<String, Future<Pair<List<ITEM_TYPE>, List<JobResponse<ITEM_TYPE>>>>> resultMap;

    private final ThreadFactory threadFactory = new BatchExecutionThreadFactory(this.getClass().getSimpleName());

    private ExecutorService threadPool;

    /**
     * If not null, thread pool gets initialized with at most maxThreadCount threads.
     */
    private Integer maxThreadCount = null;
    private AtomicInteger processedCount;

    @Override
    protected void setupExecution(final Map<String, Collection<ITEM_TYPE>> chunks) {
        Preconditions.checkNotNull(chunks, "Passed null chunks collection.");
        Preconditions.checkArgument(threadPool == null,
            "Thread pool is already initialized. Every instance should be used only once");

        final int chunkSize = chunks.keySet().size();

        // TODO: provide way to set maxThreadCount
        final int numThreads = Math.min(maxThreadCount == null ? chunkSize + 1 : maxThreadCount, chunkSize);

        LOG.info("Creating executor pool with {} threads for {} chunks.", new Object[] {numThreads, chunkSize});

        threadPool = Executors.newFixedThreadPool(numThreads, threadFactory);
        resultMap = Maps.newHashMap();

        processedCount = new AtomicInteger(0);

    }

    @Override
    protected void cleanup(final Map<String, Collection<ITEM_TYPE>> chunks) throws InterruptedException {
        LOG.info("Shutting down thread pool.");

        // shutdown the thread pool
        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.DAYS);
    }

    @Override
    public void processChunk(final String chunkId, final Collection<ITEM_TYPE> items) throws Exception {

        // clone the flowid:
        final Stack<?> cloneStack = FlowId.cloneStack();
        final String userContext = FlowUserContext.getUserContext();

        final Future<Pair<List<ITEM_TYPE>, List<JobResponse<ITEM_TYPE>>>> f = threadPool.submit(
                new Callable<Pair<List<ITEM_TYPE>, List<JobResponse<ITEM_TYPE>>>>() {

                    @Override
                    public Pair<List<ITEM_TYPE>, List<JobResponse<ITEM_TYPE>>> call() {

                        // inherit the existing flow for this thread:
                        FlowId.inherit(cloneStack);
                        FlowUserContext.setUserContext(userContext);
                        try {

                            final List<ITEM_TYPE> successfulItems = Lists.newLinkedList();
                            final List<JobResponse<ITEM_TYPE>> failedItems = Lists.newLinkedList();

                            final List<ITEM_TYPE> successfulChunkItems = Lists.newLinkedList();
                            final List<JobResponse<ITEM_TYPE>> failedChunkItems = Lists.newLinkedList();

                            LOG.debug("Starting execution of chunk id {} on [{}] ({} items)",
                                new Object[] {chunkId, Thread.currentThread().getName(), items.size()});

                            for (final ITEM_TYPE item : items) {

                                LOG.trace("Dispatching item [{}:{}] to processor [{}].",
                                    new Object[] {chunkId, item, Thread.currentThread().getName()});

                                try {
                                    processor.validate(item);
                                    processor.process(item);
                                    processedCount.getAndIncrement();

                                    successfulChunkItems.add(item);

                                    if (holdResults()) {
                                        successfulItems.add(item);
                                    }
                                } catch (final Throwable t) {
                                    final JobResponse<ITEM_TYPE> response = new JobResponse<ITEM_TYPE>(item);
                                    response.addErrorMessage(Throwables.getStackTraceAsString(t));

                                    failedChunkItems.add(response);

                                    if (holdResults()) {
                                        failedItems.add(response);
                                    }
                                }

                                if (writeTime == WriteTime.AT_EACH_ITEM) {
                                    try {
                                        write(successfulChunkItems, failedChunkItems);
                                        successfulChunkItems.clear();
                                        failedChunkItems.clear();
                                    } catch (RuntimeException e) {
                                        LOG.error("Could not write item [{}:{}]", new Object[] {chunkId, item, e});
                                        throw e;
                                    }
                                }
                            }

                            if (writeTime == WriteTime.AT_EACH_CHUNK) {
                                try {
                                    write(successfulChunkItems, failedChunkItems);
                                    successfulChunkItems.clear();
                                    failedChunkItems.clear();
                                } catch (RuntimeException e) {
                                    LOG.error("Could not write chunk [{}]", chunkId, e);
                                    throw e;
                                }
                            }

                            return Pair.of(successfulItems, failedItems);
                        } finally {
                            FlowId.clear();
                            FlowUserContext.clear();
                        }
                    }

                });
        resultMap.put(chunkId, f);
    }

    @Override
    public int getProcessedCount() {

        /*
         * In case the job had nothing to do setupExecution would not have been
         * called, thus processedCount is still null, so we hack a return of 0.
         */
        if (processedCount == null) {
            LOG.debug("Job finished without real work. Explicitly returning processedCount = 0");
            return 0;
        }

        return processedCount.get();
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

                final Pair<List<ITEM_TYPE>, List<JobResponse<ITEM_TYPE>>> futureResult = future.get();
                LOG.trace("Joining succ (prev {}, will add {})",
                    new Object[] {successes.size(), futureResult.getFirst().size()});
                successes.addAll(futureResult.getFirst());
                LOG.trace("Joining fail (prev {}, will add {})",
                    new Object[] {failures.size(), futureResult.getSecond().size()});
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

    public void setMaxThreadCount(final Integer maxThreadCount) {
        this.maxThreadCount = maxThreadCount;
    }
}
