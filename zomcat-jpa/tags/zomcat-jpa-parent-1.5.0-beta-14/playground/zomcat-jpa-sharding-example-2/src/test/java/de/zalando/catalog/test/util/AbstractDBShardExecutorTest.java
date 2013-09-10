package de.zalando.catalog.test.util;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;

// @RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:testContext.xml"})
public abstract class AbstractDBShardExecutorTest extends AbstractTest {
    @Autowired
    protected ITSProcService sprocService;

    public void executeOnAllShards(final String sql) {
        sprocService.executeOnAllShards(sql);
    }
}
