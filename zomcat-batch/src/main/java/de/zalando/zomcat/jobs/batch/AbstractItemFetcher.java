package de.zalando.zomcat.jobs.batch;

import java.util.List;

/**
 * Fetches items from the Database or the File System.
 *
 * @param   <Item>
 *
 * @author  teppel
 */
public abstract class AbstractItemFetcher<Item extends JobItem> implements ItemFetcher<Item> {

    /**
     * Fetches items.
     *
     * @param   limit
     *
     * @return
     *
     * @throws  Exception
     */
    public abstract List<Item> fetchItems(int limit) throws Exception;

}
