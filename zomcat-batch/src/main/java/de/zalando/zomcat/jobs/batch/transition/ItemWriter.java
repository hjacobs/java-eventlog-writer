package de.zalando.zomcat.jobs.batch.transition;

import java.util.Collection;

/**
 * Writes status of the processed items to the Database and External Systems. These operations should be handled in
 * bulk.
 *
 * @param   <Item>
 *
 * @author  teppel
 */
public interface ItemWriter<Item> {

    String WRITE_LOG_FORMAT = "writing {} successful and {} failed items";

    /**
     * Implementations must be thread safe for any usage under concurrency.
     *
     * @param  successfulItems
     * @param  failedItems
     */
    void writeItems(final Collection<Item> successfulItems, final Collection<JobResponse<Item>> failedItems);

}
