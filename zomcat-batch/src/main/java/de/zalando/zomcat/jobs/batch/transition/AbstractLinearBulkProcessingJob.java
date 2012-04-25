package de.zalando.zomcat.jobs.batch.transition;

import de.zalando.zomcat.jobs.batch.transition.strategy.LinearBulkProcessingExecutionStrategy;

public abstract class AbstractLinearBulkProcessingJob<ITEM_TYPE> extends AbstractBulkProcessingJob<ITEM_TYPE> {
    @Override
    protected BatchExecutionStrategy<ITEM_TYPE> getExecutionStrategy() {
        return new LinearBulkProcessingExecutionStrategy<ITEM_TYPE>();
    }
}
