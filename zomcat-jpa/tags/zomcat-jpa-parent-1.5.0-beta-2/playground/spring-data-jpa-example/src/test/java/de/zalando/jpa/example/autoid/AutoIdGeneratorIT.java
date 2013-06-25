package de.zalando.jpa.example.autoid;

import javax.sql.DataSource;

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

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import de.zalando.jpa.config.DataSourceConfig;
import de.zalando.jpa.springframework.ExtendedEclipseLinkJpaVendorAdapter;

/**
 * @author  jbellmann
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
@ActiveProfiles("POSTGRES")
public class AutoIdGeneratorIT {

    private static final Logger LOG = LoggerFactory.getLogger(AutoIdGeneratorTest.class);

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Before
    public void setUp() {
        Assert.assertNotNull(customerOrderRepository);
    }

    @Test
    public void insertCustomerOrder() {
        CustomerOrder co = new CustomerOrder();

        //
        for (int i = 0; i < 100; i++) {
            OrderLine ol = new OrderLine();
            ol.setDescription("DESCRIPTION " + i);
            co.addOrderLine(ol);
        }

        co = customerOrderRepository.saveAndFlush(co);
        LOG.info(co.toString());
    }

    @Configuration
    @Import({ DataSourceConfig.class })
    @EnableJpaRepositories("de.zalando.jpa.example.autoid")
    @EnableJpaAuditing
    static class TestConfig {

        @Autowired
        private DataSource dataSource;

        @Bean
        public PlatformTransactionManager transactionManager() {
            JpaTransactionManager transactionManager = new JpaTransactionManager();
            transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

            return transactionManager;
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            ExtendedEclipseLinkJpaVendorAdapter vendorAdapter = new ExtendedEclipseLinkJpaVendorAdapter();
            vendorAdapter.setDatabase(Database.POSTGRESQL);
            vendorAdapter.setGenerateDdl(true);
            vendorAdapter.setShowSql(true);

            LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
            factory.setPersistenceUnitName("autoIdGenerator");
            factory.setJpaVendorAdapter(vendorAdapter);
            factory.setDataSource(dataSource);

            return factory;
        }

    }

}
