package de.zalando.jpa.example.id;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author  jbellmann
 */
public interface WorkerRepository extends JpaRepository<Worker, Long> { }
