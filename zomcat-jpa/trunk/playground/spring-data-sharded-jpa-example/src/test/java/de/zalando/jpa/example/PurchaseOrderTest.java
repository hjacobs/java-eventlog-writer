package de.zalando.jpa.example;

import org.junit.Ignore;
import org.junit.Test;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import de.zalando.jpa.config.TestProfiles;

/**
 * @author  jbellmann
 */
@ActiveProfiles(TestProfiles.POSTGRES)
@DirtiesContext
@Ignore // no db-access in unit-tests at zalando
public class PurchaseOrderTest extends AbstractPurchaseOrderTestSupport {

    @Test
// @Rollback(false)
    public void testSavePurchaseOrder() {
        super.doTestSavePurchaseOrder();
    }
}
