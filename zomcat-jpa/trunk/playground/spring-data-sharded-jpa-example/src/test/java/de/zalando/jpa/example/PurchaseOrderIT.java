package de.zalando.jpa.example;

import org.junit.Test;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import de.zalando.jpa.config.TestProfiles;

/**
 * @author  jbellmann
 */
@ActiveProfiles(TestProfiles.POSTGRES)
@DirtiesContext
public class PurchaseOrderIT extends AbstractPurchaseOrderTestSupport {

    @Test
    public void testSavePurchaseOrder() {
        super.doTestSavePurchaseOrder();
    }
}
