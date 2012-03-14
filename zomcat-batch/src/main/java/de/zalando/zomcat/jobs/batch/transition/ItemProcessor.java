package de.zalando.zomcat.jobs.batch.transition;

import java.util.Collection;

import de.zalando.utils.Pair;

/**
 * Processes one item at a time. Updates only necessary status. Otherwise these should be handled by the writer
 * implementations.
 *
 * @param   <Item>
 *
 * @author  teppel
 */
public interface ItemProcessor<Item> {

    /**
     * Defines the processing of an item.
     *
     * @param   item
     *
     * @throws  ItemProcessorException
     */
    void process(Item item) throws Exception;

    /**
     * @param   items
     *
     * @return
     */
    Pair<Collection<Item>, Collection<JobResponse<Item>>> validate(final Collection<Item> items);
}
