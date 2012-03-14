package de.zalando.zomcat.jobs.batch.transition;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

import de.zalando.utils.Pair;

/**
 * Single thread execution strategy. Executes each item in a chunk after the other. Concrete implementations must define
 * the chunking strategy.
 *
 * @param   <ITEM_TYPE>
 *
 * @author  john
 */
public abstract class SingleThreadedChunkedBatchExecutionStrategy<ITEM_TYPE> extends BatchExecutionStrategy<ITEM_TYPE> {

    private final Logger LOG = LoggerFactory.getLogger(SingleThreadedChunkedBatchExecutionStrategy.class);

    private int processedCount = 0;

    @Override
    public void processChunk(final String chunkId, final Collection<ITEM_TYPE> items) throws Exception {

        Pair<List<ITEM_TYPE>, List<JobResponse<ITEM_TYPE>>> statuses = getStatuses();
        List<ITEM_TYPE> successfulItems = statuses.getFirst();
        List<JobResponse<ITEM_TYPE>> failedItems = statuses.getSecond();

        LOG.trace("Starting execution of chunk {}.", chunkId);

        for (ITEM_TYPE item : items) {

            LOG.trace("Dispatching item {} to processor.", item);

            try {
                processedCount++;
                processor.process(item);
                successfulItems.add(item);
            } catch (Throwable t) {
                JobResponse<ITEM_TYPE> response = new JobResponse<ITEM_TYPE>(item);
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

    }

    @Override
    public int getProcessedCount() {
        return processedCount;
    }
}
