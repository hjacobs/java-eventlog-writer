package de.zalando.jpa.example.order;

import org.junit.Test;

import org.springframework.test.annotation.DirtiesContext;

/**
 * @author  jbellmann
 */
@DirtiesContext
public class PurchaseOrderIT extends AbstractPurchaseOrderTestSupport {

    @Test
    public void testSavePurchaseOrder() {
        super.doTestSavePurchaseOrder();
    }
}
