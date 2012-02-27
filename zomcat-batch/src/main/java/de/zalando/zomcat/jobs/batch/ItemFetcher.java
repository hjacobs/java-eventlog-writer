package de.zalando.zomcat.jobs.batch;

import java.util.List;

/**
 * Fetches items from the Database or the File System.
 *
 * @param   <Item>
 *
 * @author  teppel
 */
public interface ItemFetcher<Item extends JobItem> {

    /**
     * Fetches items.
     *
     * @param   limit
     *
     * @return
     *
     * @throws  Exception
     */
    List<Item> fetchItems(int limit) throws Exception;

}
