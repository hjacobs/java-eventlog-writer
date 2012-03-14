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

    void writeItems(Collection<Item> successfulItems, Collection<JobResponse<Item>> failedItems) throws Exception;

}
