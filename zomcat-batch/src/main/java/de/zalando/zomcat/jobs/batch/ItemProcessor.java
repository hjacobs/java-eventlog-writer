package de.zalando.zomcat.jobs.batch;

import java.util.List;

/**
 * Processes one item at a time. Updates only necessary status. Otherwise these should be handled by the writer
 * implementations.
 *
 * @param   <Item>
 *
 * @author  teppel
 */
public interface ItemProcessor<Item> {

    void process(Item item) throws Exception;

    List<String> validate(final Item item);

}
