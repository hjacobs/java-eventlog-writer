package de.zalando.catalog.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.transaction.annotation.Transactional;

import de.zalando.catalog.domain.ShardedId;
import de.zalando.catalog.domain.multimedia.Multimedia;

public interface MultimediaRepository extends JpaRepository<Multimedia, ShardedId> {

    @Transactional
    Multimedia findByCode(final ShardedId code);

    @Query("select m from Multimedia m where m.code in (?1)")
    List<Multimedia> findByCodes(final List<ShardedId> codes);

}
