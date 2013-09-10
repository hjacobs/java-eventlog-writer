package de.zalando.jpa.example.id;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * An {@link JpaRepository} with custom definition.
 *
 * @author  jbellmann
 */
public interface WorkerConfigRepository extends JpaRepository<WorkerConfig, WorkerConfigPK>,
    WorkerConfigRepositoryCustom { }
