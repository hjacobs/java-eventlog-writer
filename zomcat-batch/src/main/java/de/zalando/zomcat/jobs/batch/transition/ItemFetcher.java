package de.zalando.zomcat.jobs.batch.transition;

import java.util.List;

/**
 * Fetches items from the Database or the File System.
 *
 * @param   <Item>
 *
 * @author  teppel
 */
public interface ItemFetcher<Item> {

    /**
     * Fetches items.
     *
     * @param   limit
     *
     * @return
     *
     * @throws  ItemFetcherException
     */
    List<Item> fetchItems(int limit) throws Exception;

    /**
     * Possibly adds additional information, which comes from different sources. These requests should be handled in
     * bulk. A trivial implementation should simply return the parameter.
     *
     * @param   items
     *
     * @return
     *
     * @throws  ItemFetcherException
     */
    List<Item> enrichItems(final List<Item> items) throws Exception;

}
