package de.zalando.zomcat.jobs.batch.transition;

public abstract class AbstractLinearBulkProcessingJob<ITEM_TYPE> extends AbstractBulkProcessingJob<ITEM_TYPE> {
    @Override
    protected ItemPartitioningStrategy<ITEM_TYPE> getPartitioningStrategy() {
        return new LinearBulkProcessingPartitioningStrategy<ITEM_TYPE>();
    }
}
