package de.zalando.zomcat.jobs.batch.transition;

import java.util.Collection;
import java.util.Collections;

import de.zalando.utils.Pair;

public abstract class NoValidationItemProcessor<Item> implements ItemProcessor<Item> {

    @Override
    public final Pair<Collection<Item>, Collection<JobResponse<Item>>> validate(final Collection<Item> items) {

        Collection<JobResponse<Item>> empty = Collections.emptyList();
        return Pair.of(items, empty);
    }

}
