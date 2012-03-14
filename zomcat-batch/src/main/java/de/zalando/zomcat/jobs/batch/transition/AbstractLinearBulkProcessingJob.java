package de.zalando.zomcat.jobs.batch.transition;

public abstract class AbstractLinearBulkProcessingJob<ITEM_TYPE> extends AbstractBulkProcessingJob<ITEM_TYPE> {
    @Override
    protected BatchExecutionStrategy<ITEM_TYPE> getExecutionStrategy() {
        return new LinearBulkProcessingExecutionStrategy<ITEM_TYPE>();
    }
}
