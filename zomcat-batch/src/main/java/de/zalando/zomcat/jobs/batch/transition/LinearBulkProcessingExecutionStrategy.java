package de.zalando.zomcat.jobs.batch.transition;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Simple linear partitioning strategy and execution. Build and returns one single chunk with all the given work items.
 * This is the simplest complete execution strategy possible, consisting exactly of the original
 * AbstractBulkProcessingJob behaviour.
 *
 * @author  john
 */
public class LinearBulkProcessingExecutionStrategy<ITEM_TYPE> extends SingleThreadedChunkedBatchExecutionStrategy<ITEM_TYPE> {

    @Override
    public Map<String, Collection<ITEM_TYPE>> makeChunks(final Collection<ITEM_TYPE> items) {

        Map<String, Collection<ITEM_TYPE>> r = Maps.newHashMap();
        r.put("__SINGLE_CHUNK", items);

        return r;
    }

    public WriteTime getWriteTime() {
        return WriteTime.AT_END_OF_BATCH;
    }

}
