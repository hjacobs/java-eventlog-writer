package de.zalando.catalog.backend.repository;

import de.zalando.catalog.domain.ShardedId;
import de.zalando.catalog.domain.multimedia.Multimedia;

/**
 * @author  jbellmann
 * @see     MultimediaPredicates for more ways to handle repository-queries
 */
public interface MultimediaRepositoryCustom {

    Multimedia findByShardedId(final ShardedId shardedId);

// List<Multimedia> findByCodes(final List<ShardedId> codes);

}
