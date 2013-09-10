package de.zalando.jpa.example.unidirectional;

import org.springframework.test.context.ActiveProfiles;

import de.zalando.jpa.config.TestProfiles;

/**
 * Test against Postgres-Database in integration-test.
 *
 * @author  jbellmann
 */
@ActiveProfiles(TestProfiles.POSTGRES)
public class PostgresUnidirectionalIT extends AbstractUnidirectionalTest { }
