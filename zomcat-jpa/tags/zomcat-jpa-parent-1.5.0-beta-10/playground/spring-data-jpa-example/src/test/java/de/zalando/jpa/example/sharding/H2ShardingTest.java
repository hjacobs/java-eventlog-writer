package de.zalando.jpa.example.sharding;

import org.junit.Ignore;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import de.zalando.jpa.config.TestProfiles;

@ActiveProfiles(TestProfiles.H2_SHARDED)
@DirtiesContext
@Ignore
public class H2ShardingTest extends AbstractShardingTest { }
