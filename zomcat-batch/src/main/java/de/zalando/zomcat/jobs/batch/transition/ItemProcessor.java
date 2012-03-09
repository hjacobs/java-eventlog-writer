package de.zalando.zomcat.jobs.batch.transition;

import java.util.Collection;
import java.util.Collections;

import de.zalando.utils.Pair;

/**
 * Processes one item at a time. Updates only necessary status. Otherwise these should be handled by the writer
 * implementations.
 *
 * @param   <Item>
 *
 * @author  teppel
 */
public abstract class ItemProcessor<Item> {

    public abstract void process(JobResponse<Item> item) throws ItemProcessorException;

    public Pair<Collection<JobResponse<Item>>, Collection<JobResponse<Item>>> validate(
            final Collection<JobResponse<Item>> items) {
        Collection<JobResponse<Item>> empty = Collections.emptyList();
        return Pair.of(items, empty);
    }

}
