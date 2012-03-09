package de.zalando.zomcat.jobs.batch.transition;

import java.util.List;

/**
 * Fetches items from the Database or the File System.
 *
 * @param   <Item>
 *
 * @author  teppel
 */
public abstract class ItemFetcher<Item> {

    /**
     * Fetches items.
     *
     * @param   limit
     *
     * @return
     *
     * @throws  ItemFetcherException
     */
    public abstract List<JobResponse<Item>> fetchItems(int limit) throws ItemFetcherException;

    /**
     * Possibly adds additional information, which comes from different sources. These requests should be handled in
     * bulk.
     *
     * @param   items
     *
     * @return
     *
     * @throws  ItemFetcherException
     */
    public List<JobResponse<Item>> enrichItems(final List<JobResponse<Item>> items) throws ItemFetcherException {
        return items;
    }

}
