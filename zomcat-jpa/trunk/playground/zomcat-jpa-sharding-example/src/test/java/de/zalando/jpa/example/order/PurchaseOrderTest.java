package de.zalando.jpa.example.order;

import org.junit.Ignore;
import org.junit.Test;

import org.springframework.test.annotation.DirtiesContext;

/**
 * @author  jbellmann
 */
@DirtiesContext
@Ignore // no db-access in unit-tests at zalando
public class PurchaseOrderTest extends AbstractPurchaseOrderTestSupport {

    @Test
// @Rollback(false)
    public void testSavePurchaseOrder() {
        super.doTestSavePurchaseOrder();
    }
}
