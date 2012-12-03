package de.zalando.zomcat.jobs.batch.transition;

import java.util.Map;

import org.quartz.JobExecutionContext;

public abstract class NoValidationItemProcessor<Item> implements ItemProcessor<Item> {

    @Override
    public void validate(final Item item, final JobExecutionContext jobExecutionContext,
            final Map<String, Object> localExecutionContext) {
        // no op
    }

}
