package de.zalando.zomcat.jobs.batch.transition;

import java.util.Collection;

/**
 * This interface allows the execution of one more step after all writers.
 *
 * @param   <Item>
 *
 * @author  pribeiro
 */
public interface ItemFinalizer<Item> {

    /**
     * Executes one final step with with all aggregated data.
     *
     * @param  successfulItems
     * @param  failedItems
     */
    void finalizeItems(final Collection<Item> successfulItems, final Collection<JobResponse<Item>> failedItems);

}
