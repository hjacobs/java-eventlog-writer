package de.zalando.jpa.example.sharding;

import org.junit.Ignore;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import de.zalando.jpa.config.TestProfiles;

@ActiveProfiles(TestProfiles.HSQL)
@DirtiesContext
@Ignore
public class HsqlShardingTest extends AbstractShardingTest { }
