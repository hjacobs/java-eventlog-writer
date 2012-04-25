package de.zalando.zomcat.jobs.batch.transition;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private List<Item> successfulItems;
    private List<JobResponse<Item>> failedItems;

    public void bind(final ItemProcessor<Item> processor, final ItemWriter<Item> writer, final WriteTime writeTime,
            final List<Item> successfulItems, final List<JobResponse<Item>> failedItems) {
        this.processor = processor;
        this.writer = writer;
        this.writeTime = writeTime;
        this.successfulItems = successfulItems;
        this.failedItems = failedItems;
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
    public abstract Map<String, Collection<Item>> makeChunks(Collection<Item> items);

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
    public abstract void processChunk(final String chunkId, Collection<Item> items) throws Exception;

    /**
     * Receives the fetched items, splits them into chunks (according to the given strategy) and dispatches each of them
     * to be executed.
     *
     * @param   items
     *
     * @throws  Exception
     */
    public void execute(final Collection<Item> items) throws Exception {

// // if we are working with a write back time that is not at the end, we flush right now the failures that we got.
// // TODO: decide if this should be done at the end instead of at the beginning.
// if (writeTime != WriteTime.AT_END_OF_BATCH) {
//
// LOG.trace("Flushing errors (validation): [{}], [{}]", new Object[] {successfulItems, failedItems});
//
// write(successfulItems, failedItems);
// failedItems.clear();
//
// // it shouldn't really be necessary to flush successfulItems since it should be empty here.
// successfulItems.clear();
// }

        Map<String, Collection<Item>> chunks = makeChunks(items);
        if (chunks == null) {
            throw new IllegalStateException("Chunker MUST NOT return null!");
        }

        Set<String> keySet = chunks.keySet();

        int i = 0;

        for (String k : keySet) {
            i++;
            LOG.trace("Dispatching chunk {} of {} ({}).", new Object[] {i, chunks.size(), k});
            processChunk(k, chunks.get(k));
        }

        if (writeTime == WriteTime.AT_END_OF_BATCH) {
            write(successfulItems, failedItems);
        }

    }

    /**
     * @param   successfulItems
     * @param   failedItems
     *
     * @throws  Exception
     */
    protected void write(final Collection<Item> successfulItems, final Collection<JobResponse<Item>> failedItems)
        throws Exception {
        LOG.debug(ItemWriter.WRITE_LOG_FORMAT, successfulItems.size(), failedItems.size());
        writer.writeItems(successfulItems, failedItems);
    }

    protected Pair<List<Item>, List<JobResponse<Item>>> getStatuses() {
        return Pair.of(successfulItems, failedItems);
    }

    public abstract int getProcessedCount();
}
