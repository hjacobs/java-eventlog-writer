package de.zalando.zomcat.jobs.batch.transition;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

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

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    protected ItemProcessor<Item> processor;
    protected WriteTime writeTime;
    protected ItemWriter<Item> writer;

    public void bind(final ItemProcessor<Item> processor, final ItemWriter<Item> writer, final WriteTime writeTime) {
        this.processor = processor;
        this.writer = writer;
        this.writeTime = writeTime;

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
    public final void execute(final Collection<Item> items) throws Exception {

        Map<String, Collection<Item>> chunks = makeChunks(items);
        if (chunks == null) {
            throw new IllegalStateException("Invariant violated: Chunker MUST NOT return null!");
        }

        if (!allItemsAreInChunks(items, chunks)) {
            throw new IllegalStateException("Invariant violated: not all fetched items are present in chunks!");
        }

        setupExecution(chunks);

        try {
            Set<String> keySet = chunks.keySet();

            int i = 0;

            for (String k : keySet) {
                i++;
                LOG.trace("Dispatching chunk {} of {} ({}).", new Object[] {i, chunks.size(), k});
                executeChunk(k, chunks.get(k));
            }

            if (writeTime == WriteTime.AT_END_OF_BATCH) {

                Pair<List<Item>, List<JobResponse<Item>>> r = joinResults();

                Collection<Item> successfulItems = r.getFirst();
                Collection<JobResponse<Item>> failedItems = r.getSecond();

                write(successfulItems, failedItems);
            }
        } finally {
            cleanup(chunks);
        }
    }

    private void executeChunk(final String chunkId, final Collection<Item> items) throws Exception {

        processChunk(chunkId, items);

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

        Set<String> keySet = chunks.keySet();
        for (String k : keySet) {
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
     */
    protected void cleanup(final Map<String, Collection<Item>> chunks) { }

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

    /*protected Pair<List<Item>, List<JobResponse<Item>>> getStatuses() {
     *  return Pair.of(successfulItems, failedItems);
     *}*/

    public abstract int getProcessedCount();

    /**
     * Validate the input items with the JSR-303 implementation. If violations are found the items is sorted to the
     * failed list, otherwise to the successful list.
     *
     * @param   items
     *
     * @return  a Pair of lists: first is the successful, second is the failed list
     */
    private JobResponse<Item> validate(final Item item) {
        // final Collection<Item> successfulItems = Lists.newArrayList();
        // final Collection<JobResponse<Item>> failedItems = Lists.newArrayList();

        JobResponse<Item> response = null;

        // for (final Item item : items) {
        final Set<ConstraintViolation<Item>> violations = VALIDATOR.validate(item);
        if (violations.isEmpty()) {
            // successfulItems.add(item);
        } else {
            final List<String> messages = getMessageList(violations);
            response = new JobResponse<Item>(item);
            response.addErrorMessages(messages);

            // failedItems.add(response);
            LOG.warn("item [{}] failed validation [{}]", item, messages);
        }
        // }

        return response;
            // return Pair.of(successfulItems, failedItems);
    }

    private static <T> List<String> getMessageList(final Set<ConstraintViolation<T>> violations) {
        List<String> messages = Lists.newArrayListWithCapacity(violations.size());
        for (final ConstraintViolation<?> violation : violations) {
            messages.add(String.format("invalid value [%s] of property [%s] %s", violation.getInvalidValue(),
                    violation.getPropertyPath(), violation.getMessage()));
        }

        return messages;
    }
}
