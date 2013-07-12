package de.zalando.jpa.example.sharding;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.springframework.data.jpa.auditing.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.transaction.annotation.Transactional;

import de.zalando.jpa.config.DataSourceConfig;
import de.zalando.jpa.config.DefaultPersistenceUnitNameProvider;
import de.zalando.jpa.config.JpaConfig;
import de.zalando.jpa.config.PersistenceUnitNameProvider;
import de.zalando.jpa.config.VendorAdapterDatabaseConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class AbstractShardingTest {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractShardingTest.class);

    public static final String packagesToScan = "de.zalando.jpa.example.sharding";
    public static final String persistenceUnitName = "sharding";

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Before
    public void setUp() {
        Assert.assertNotNull(customerOrderRepository);
    }

    @Test
    public void insertCustomerOrder() {
        CustomerOrder co = new CustomerOrder();
        co = customerOrderRepository.saveAndFlush(co);

        //
        for (int i = 0; i < 3; i++) {
            OrderLine ol = new OrderLine();
            ol.setDescription("DESCRIPTION " + i);
            co.addOrderLine(ol);
        }

        co = customerOrderRepository.saveAndFlush(co);
        LOG.info(co.toString());
    }

    @Configuration
    @EnableJpaRepositories(AbstractShardingTest.packagesToScan)
    @EnableJpaAuditing
    @Import({ JpaConfig.class, DataSourceConfig.class, VendorAdapterDatabaseConfig.class })
    static class TestConfig {

        @Bean
        public PersistenceUnitNameProvider persistenceUnitNameProvider() {
            return new DefaultPersistenceUnitNameProvider(AbstractShardingTest.persistenceUnitName);
        }

    }
}
