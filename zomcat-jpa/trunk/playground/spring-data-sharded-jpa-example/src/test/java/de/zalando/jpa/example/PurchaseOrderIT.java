package de.zalando.jpa.example;

import org.junit.Test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import de.zalando.jpa.config.DefaultPersistenceUnitNameProvider;
import de.zalando.jpa.config.PersistenceUnitNameProvider;
import de.zalando.jpa.config.TestProfiles;

/**
 * @author  jbellmann
 */
// we have to use a different persistenceUnitName for this test.
@ContextConfiguration
@ActiveProfiles(TestProfiles.POSTGRES)
@DirtiesContext
public class PurchaseOrderIT extends AbstractPurchaseOrderTestSupport {

    @Test
    public void testSavePurchaseOrder() {
        super.doTestSavePurchaseOrder();
    }

    @Configuration
    static class ITConfig {

        @Bean
        public PersistenceUnitNameProvider persistenceUnitNameProvider() {
            return new DefaultPersistenceUnitNameProvider("integrationTest");
        }

    }
}
