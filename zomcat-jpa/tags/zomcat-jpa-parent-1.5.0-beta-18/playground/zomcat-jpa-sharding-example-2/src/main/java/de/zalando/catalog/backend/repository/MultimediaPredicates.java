package de.zalando.catalog.backend.repository;

import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import com.mysema.query.types.Predicate;

import de.zalando.catalog.domain.ShardedId;
import de.zalando.catalog.domain.multimedia.Multimedia;
import de.zalando.catalog.domain.multimedia.QMultimedia;

/**
 * Some Predicates for {@link Multimedia}.
 *
 * @author  jbellmann
 */
public final class MultimediaPredicates {

    private MultimediaPredicates() { }

    public static Predicate idIn(final List<Long> ids) {

        QMultimedia qMultimedia = QMultimedia.multimedia;
        return qMultimedia.id.in(ids);
    }

    public static List<Long> transform(final List<ShardedId> shardIds) {
        return newArrayList(Iterables.transform(filter(shardIds, notNull()), new ExtractId()));
    }

    /**
     * To extract the id from an {@link ShardedId}.
     *
     * @author  jbellmann
     */
    static final class ExtractId implements Function<ShardedId, Long> {

        @Override
        @Nullable
        public Long apply(@Nullable final ShardedId input) {
            return input.asLong();
        }
    }
}
