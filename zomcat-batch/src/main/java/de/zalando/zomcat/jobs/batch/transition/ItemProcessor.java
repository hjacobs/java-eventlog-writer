package de.zalando.zomcat.jobs.batch.transition;

import java.util.Map;

import org.quartz.JobExecutionContext;

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
    void process(final Item item, final JobExecutionContext jobExecutionContext,
            final Map<String, Object> localExecutionContext) throws Exception;

    /**
     * @param   items
     *
     * @return
     */
    void validate(final Item item, final JobExecutionContext jobExecutionContext,
            final Map<String, Object> localExecutionContext);

}
