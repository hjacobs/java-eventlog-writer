package de.zalando.zomcat.jobs.batch.transition;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Simple linear partitioning strategy. Build and returns one single chunck with all the given work items.
 *
 * @author  john
 */
public class LinearBulkProcessingPartitioningStrategy<ITEM_TYPE> extends ItemPartitioningStrategy<ITEM_TYPE> {

    @Override
    public Map<String, Collection<JobResponse<ITEM_TYPE>>> makeChunks(final Collection<JobResponse<ITEM_TYPE>> items) {

        Map<String, Collection<JobResponse<ITEM_TYPE>>> r = Maps.newHashMap();
        r.put("0", items);

        return r;
    }
}
