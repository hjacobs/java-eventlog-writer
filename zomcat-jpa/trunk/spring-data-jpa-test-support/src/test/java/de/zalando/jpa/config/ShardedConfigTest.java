package de.zalando.jpa.config;

import java.util.Map;

import javax.persistence.EntityManagerFactory;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author  jbellmann
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@ActiveProfiles(TestProfiles.H2_SHARDED_4)
public class ShardedConfigTest {

    @Autowired(required = false)
    @Qualifier("defaultDataSource")
    private DataSource defaultDataSource;

    @Autowired(required = false)
    private Map<String, DataSource> dataSourceLookup;

    @Autowired(required = false)
    private EntityManagerFactory entityManagerFactory;

    @Test
    public void defaultDataSourceWired() {
        Assert.assertNotNull(defaultDataSource);
    }

    @Test
    public void dataSourceLookupWired() {
        Assert.assertNotNull(dataSourceLookup);
        Assert.assertFalse(dataSourceLookup.values().isEmpty());
    }

    @Test
    public void entityManagerFactoryWired() {
        Assert.assertNotNull(entityManagerFactory);
    }

    @Configuration
    @Import({ ShardedDataSourceConfig.class, ShardedJpaConfig.class, VendorAdapterDatabaseConfig.class })
    static class TestConfig { }
}
