package de.zalando.jpa.example.auditing;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Just for testing.
 *
 * @author  jbellmann
 */
public interface AccountRepository extends JpaRepository<Account, Long> { }
