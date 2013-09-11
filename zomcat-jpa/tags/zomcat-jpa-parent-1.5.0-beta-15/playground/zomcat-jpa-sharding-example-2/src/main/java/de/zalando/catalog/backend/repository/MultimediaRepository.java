package de.zalando.catalog.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import de.zalando.catalog.domain.multimedia.Multimedia;

/**
 * Have a look to {@link MultimediaPredicates} and {@link MultimediaRepositoryCustom}, {@link MultimediaRepositoryImpl}
 * for customization.
 *
 * @author  jbellmann
 */
public interface MultimediaRepository extends JpaRepository<Multimedia, Long>, MultimediaRepositoryCustom,
    QueryDslPredicateExecutor<Multimedia> {

// Multimedia findByCode(final ShardedId code);

// @Query("select m from Multimedia m where m.code in (?1)")
// List<Multimedia> findByCodes(final List<ShardedId> codes);

}
