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
public abstract class AbstractItemProcessor<Item extends JobItem> implements ItemProcessor<Item> {

    @Override
    public abstract void process(Item item) throws Exception;

    @Override
    public List<String> validate(final Item item) {
        return null;
    }

}
