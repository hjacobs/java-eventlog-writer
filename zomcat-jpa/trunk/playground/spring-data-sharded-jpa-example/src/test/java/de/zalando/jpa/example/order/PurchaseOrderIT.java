package de.zalando.jpa.example.order;

import org.junit.Test;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;

/**
 * @author  jbellmann
 */
@DirtiesContext
public class PurchaseOrderIT extends AbstractPurchaseOrderTestSupport {

    @Test
    @Rollback(false)
    public void testSavePurchaseOrder() {
        super.doTestSavePurchaseOrder();
    }
}
