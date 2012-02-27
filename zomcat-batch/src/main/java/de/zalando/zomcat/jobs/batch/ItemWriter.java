package de.zalando.zomcat.jobs.batch;

import java.util.List;

/**
 * Writes status of the processed items to the Database and External Systems. These operations should be handled in
 * bulk.
 *
 * @param   <Item>
 *
 * @author  teppel
 */
public interface ItemWriter<Item extends JobItem> {

    void writeItems(List<JobResponse<Item>> successfulItems, List<JobResponse<Item>> failedItems) throws Exception;

}
