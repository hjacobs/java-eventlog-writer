package de.zalando.zomcat.jobs.batch.transition;

public abstract class NoValidationItemProcessor<Item> implements ItemProcessor<Item> {

    @Override
    public void validate(final Item item) {
        // no op
    }

}
