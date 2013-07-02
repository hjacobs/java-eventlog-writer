package de.zalando.jpa.example.unidirectional;

import java.util.List;

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

import com.google.common.collect.Lists;

import de.zalando.jpa.config.DataSourceConfig;
import de.zalando.jpa.config.DefaultPersistenceUnitNameProvider;
import de.zalando.jpa.config.JpaZwoConfig;
import de.zalando.jpa.config.PersistenceUnitNameProvider;
import de.zalando.jpa.config.VendorAdapterDatabaseConfig;

/**
 * Test-Logic.
 *
 * @author  jbellmann
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public abstract class AbstractUnidirectionalTest {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractUnidirectionalTest.class);

    public static final String peristenceUnitName = "de.zalando.jpa.example.unidirectional";

    @Autowired
    protected CustomerOrderRepository customerOrderRepository;

    @Autowired
    protected OrderLineRepository orderLineRepository;

    @Before
    public void setUp() {
        Assert.assertNotNull(customerOrderRepository);
        Assert.assertNotNull(orderLineRepository);
    }

    @Test
    public void insertCustomerOrder() {
        CustomerOrder co = new CustomerOrder();
        co = customerOrderRepository.saveAndFlush(co);

        // we create all all lines an save them in one call
        List<OrderLine> insertLines = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            OrderLine ol = new OrderLine();
            ol.setDescription("DESCRIPTION " + i);
            ol.setCustomerOrder(co);
            insertLines.add(ol);
        }

        //
        orderLineRepository.save(insertLines);
        orderLineRepository.flush();

        //
        List<OrderLine> lines = orderLineRepository.findByCustomerOrder(co);
        Assert.assertTrue(lines.size() == 100);

        LOG.info(co.toString());
    }

    @Configuration
    @EnableJpaRepositories(AbstractUnidirectionalTest.peristenceUnitName)
    @EnableJpaAuditing
    @Import({ JpaZwoConfig.class, DataSourceConfig.class, VendorAdapterDatabaseConfig.class })
    static class TestConfig {

        @Bean
        public PersistenceUnitNameProvider persistenceUnitNameProvider() {
            return new DefaultPersistenceUnitNameProvider("unidirectional");
        }

    }
}
