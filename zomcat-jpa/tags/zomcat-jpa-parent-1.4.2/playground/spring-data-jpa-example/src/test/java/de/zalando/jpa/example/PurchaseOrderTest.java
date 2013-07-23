package de.zalando.jpa.example;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.springframework.data.jpa.auditing.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.transaction.annotation.Transactional;

import de.zalando.jpa.config.DataSourceConfig;
import de.zalando.jpa.config.JpaConfig;

/**
 * @author  jbellmann
 */
// @Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
@ActiveProfiles(value = "HSQL")
public class PurchaseOrderTest extends AbstractPurchaseOrderTestSupport {

    @Test
    @Rollback(false)
    public void testSavePurchaseOrder() {
        super.doTestSavePurchaseOrder();
    }

    @Configuration
    @Import({ JpaConfig.class, DataSourceConfig.class })
    @EnableJpaRepositories("de.zalando.jpa.example.order")
    @EnableJpaAuditing
// @ImportResource("classpath:/enableAuditing.xml")
    static class TestConfig { }
}
