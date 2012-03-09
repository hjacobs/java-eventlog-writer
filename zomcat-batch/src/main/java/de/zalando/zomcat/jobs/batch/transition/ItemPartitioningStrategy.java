package de.zalando.zomcat.jobs.batch.transition;

import java.util.Collection;
import java.util.Map;

public abstract class ItemPartitioningStrategy<Item> {
    public abstract Map<String, Collection<JobResponse<Item>>> makeChunks(Collection<JobResponse<Item>> items);
}
