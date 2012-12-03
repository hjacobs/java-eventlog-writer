package de.zalando.zomcat.jobs.batch.transition.strategy;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import de.zalando.zomcat.jobs.batch.transition.BatchExecutionStrategy;
import de.zalando.zomcat.jobs.batch.transition.JobResponse;
import de.zalando.zomcat.jobs.batch.transition.WriteTime;

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

    protected final List<ITEM_TYPE> successfulItems = Lists.newArrayList();
    protected final List<JobResponse<ITEM_TYPE>> failedItems = Lists.newArrayList();

    @Override
    public void processChunk(final String chunkId, final Collection<ITEM_TYPE> items) throws Exception {

        LOG.trace("Starting execution of chunk {}.", chunkId);

        final int total = items.size();

        for (final ITEM_TYPE item : items) {

            LOG.trace("Dispatching item {} to processor. (%s of %s)",
                new Object[] {item, (processedCount + 1), items.size()});

            try {
                processedCount++;
                processor.validate(item, jobExecutionContext, localExecutionContext);
                processor.process(item, jobExecutionContext, localExecutionContext);

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

    }

    @Override
    public int getProcessedCount() {
        return processedCount;
    }
}
