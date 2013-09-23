package de.zalando.jpa.example.sharding;

import org.junit.Ignore;

import org.springframework.test.context.ActiveProfiles;

import de.zalando.jpa.config.TestProfiles;

/**
 * @author  jbellmann
 */
@ActiveProfiles(TestProfiles.POSTGRES)
@Ignore
public class PostgresShardingIT extends AbstractShardingTest { }
