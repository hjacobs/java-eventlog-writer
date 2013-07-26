package de.zalando.catalog.backend.repository;

import org.springframework.beans.factory.annotation.Autowired;

import de.zalando.catalog.domain.ShardedId;
import de.zalando.catalog.domain.multimedia.Multimedia;

/**
 * It is important to name this class {@link MultimediaRepository}Impl. Never name it {@link MultimediaRepositoryCustom}
 * Impl, that will not work. Spring-Data then tries to find properties named like methods.
 *
 * @author  jbellmann
 */
public class MultimediaRepositoryImpl implements MultimediaRepositoryCustom {

    @Autowired
    private MultimediaRepository multimediaRepository;

    @Override
    public Multimedia findByShardedId(final ShardedId shardedId) {

        return multimediaRepository.findOne(shardedId.asLong());
    }
}
