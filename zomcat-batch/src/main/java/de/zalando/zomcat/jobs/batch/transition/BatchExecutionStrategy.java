package de.zalando.zomcat.jobs.batch.transition;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import de.zalando.utils.Pair;

/**
 * Abstract definition of the execution strategy of the items in the batch. This specifies the way in which the items
 * are to be partitioned (if any is required) and how these partitions are to be executed (parallel, thread pool,
 * locking, etc). See the subclasses for ready made implementations of typical usages.
 *
 * @param   <Item>
 *
 * @author  john
 */
public abstract class BatchExecutionStrategy<Item> {

    private final Logger LOG = LoggerFactory.getLogger(BatchExecutionStrategy.class);

    protected ItemProcessor<Item> processor;
    protected WriteTime writeTime;
    protected ItemWriter<Item> writer;
    protected ItemFinalizer<Item> finalizer;

    public void bind(final ItemProcessor<Item> processor, final ItemWriter<Item> writer, final WriteTime writeTime,
            final ItemFinalizer<Item> finalizer) {
        this.processor = processor;
        this.writer = writer;
        this.writeTime = writeTime;
        this.finalizer = finalizer;
    }

    /**
     * Separates the fetched batch in smaller pieces to be processed together. The return is a map to allow the chunker
     * to pass some identifier to the executor (for example in case we have one thread per shard and we split the items
     * according to the shard and must pass the items to the appropriate shard).
     *
     * @param   items
     *
     * @return
     */
    public abstract Map<String, Collection<Item>> makeChunks(final Collection<Item> items);

    /**
     * Executes one chunk of data. Concrete implementations must ideally be aware of the chunking strategy that was
     * used. IMPORTANT: each concrete implementation MUST handle the collections of successful and failed items by
     * itself and respect the definitions of WriteTime that are give (specially if they are AT_EACH_CHUNK or
     * AT_EACH_ITEM).
     *
     * @param   chunkId
     * @param   items
     *
     * @throws  Exception
     */
    public abstract void processChunk(final String chunkId, final Collection<Item> items) throws Exception;

    /**
     * Receives the fetched items, splits them into chunks (according to the given strategy) and dispatches each of them
     * to be executed.
     *
     * @param   items
     *
     * @throws  Exception
     */
    public final void execute(final Collection<Item> items) throws Exception {

        final Map<String, Collection<Item>> chunks = makeChunks(items);
        if (chunks == null) {
            throw new IllegalStateException("Invariant violated: Chunker MUST NOT return null!");
        }

        if (!allItemsAreInChunks(items, chunks)) {
            throw new IllegalStateException("Invariant violated: not all fetched items are present in chunks!");
        }

        setupExecution(chunks);

        try {
            final Set<String> keySet = chunks.keySet();

            int i = 0;

            for (final String k : keySet) {
                i++;
                LOG.debug("Dispatching chunk {} of {} (chunkId: {}).", new Object[] {i, chunks.size(), k});
                executeChunk(k, chunks.get(k));
            }

            if (holdResults()) {

                final Pair<List<Item>, List<JobResponse<Item>>> r = joinResults();

                if (writeTime == WriteTime.AT_END_OF_BATCH) {
                    write(r.getFirst(), r.getSecond());
                }

                if (this.finalizer != null) {
                    this.finalizer.finalizeItems(r.getFirst(), r.getSecond());
                }
            }

        } finally {
            cleanup(chunks);
        }
    }

    private void executeChunk(final String chunkId, final Collection<Item> items) throws Exception {

        processChunk(chunkId, items);

    }

    protected boolean holdResults() {
        return writeTime == WriteTime.AT_END_OF_BATCH || finalizer != null;
    }

    /**
     * Joins together results from all possible chunks. This gets called ONLY when write time ==
     * WriteTime.AT_END_OF_BATCH
     *
     * @return
     */
    protected abstract Pair<List<Item>, List<JobResponse<Item>>> joinResults();

    private boolean allItemsAreInChunks(final Collection<Item> items, final Map<String, Collection<Item>> chunks) {

        int sumInChunks = 0;

        final Set<String> keySet = chunks.keySet();
        for (final String k : keySet) {
            sumInChunks += chunks.get(k).size();
        }

        return sumInChunks == items.size();
    }

    /**
     * Before dispatching the items to the execution, some strategies may required some additional steps (setup thread
     * pool, for example). Default implementation is noop.
     */
    protected void setupExecution(final Map<String, Collection<Item>> chunks) { }

    /**
     * Called when processing of chunks is done and results have been collected. May be used to cleanup resources used
     * during execution, e.g. closing database connection, shutting down thread pool.
     *
     * @throws  InterruptedException
     */
    protected void cleanup(final Map<String, Collection<Item>> chunks) throws InterruptedException { }

    /**
     * @param   successfulItems
     * @param   failedItems
     *
     * @throws  Exception
     */
    protected void write(final Collection<Item> successfulItems, final Collection<JobResponse<Item>> failedItems) {
        LOG.debug(ItemWriter.WRITE_LOG_FORMAT, successfulItems.size(), failedItems.size());
        writer.writeItems(successfulItems, failedItems);
    }

    public abstract int getProcessedCount();

    private static <T> List<String> getMessageList(final Set<ConstraintViolation<T>> violations) {
        final List<String> messages = Lists.newArrayListWithCapacity(violations.size());
        for (final ConstraintViolation<?> violation : violations) {
            messages.add(String.format("invalid value [%s] of property [%s] %s", violation.getInvalidValue(),
                    violation.getPropertyPath(), violation.getMessage()));
        }

        return messages;
    }
}
