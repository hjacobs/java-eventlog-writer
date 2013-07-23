package de.zalando.data.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.client.RestTemplate;

import de.zalando.data.jpa.domain.InvoiceAddress;
import de.zalando.data.jpa.domain.PurchaseOrder;

/**
 * Integration-Test to show LAZY_LOADING.
 *
 * @author  jbellmann
 */
public class PurchaseOrderServiceIT {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderServiceIT.class);

    private static final String URL_ALL = "http://localhost:9092/static-weaving/purchaseorders/all";
    private static final String LOAD = "http://localhost:9092/static-weaving/purchaseorders/{businesskey}";
    private static final String LOAD_WITH_WAIT_TIME =
        "http://localhost:9092/static-weaving/purchaseorders/{businesskey}/{timeinMillis}";
    private static final Integer TIME_IN_MILLIS = 1500;
    public static final String REPLACE_ADDRESS =
        "http://localhost:9092/static-weaving/purchaseorders/replaceAddress/{businesskey}";

    @Test
    public void test() {

        RestTemplate template = new RestTemplate();
        List<?> result = template.getForObject(URL_ALL, List.class);
        for (Object key : result) {
            LOG.info("GOT KEY : " + key);

            PurchaseOrder order = template.getForObject(LOAD_WITH_WAIT_TIME, PurchaseOrder.class, key, TIME_IN_MILLIS);
            LOG.info("GOT PO ON CLIENT : {}", order);
        }
    }

    @Test
    public void testReplaceInvoiceAddress() {
        final RestTemplate template = new RestTemplate();
        final List<?> purchaseOrders = template.getForObject(URL_ALL, List.class);
        assertFalse("Setup failed, no purchase orders available", purchaseOrders == null || purchaseOrders.isEmpty());

        final Object firstPurchaseOrder = purchaseOrders.get(0);

        final InvoiceAddress newInvoiceAddress = new InvoiceAddress();
        newInvoiceAddress.setFirstname("Bernd");
        newInvoiceAddress.setLastname("das Brot");
        newInvoiceAddress.setStreet("Sesamstrasse");
        newInvoiceAddress.setCity("Berlin");
        newInvoiceAddress.setPostcode("12345");

        template.put(REPLACE_ADDRESS, newInvoiceAddress, firstPurchaseOrder);

        final PurchaseOrder purchaseOrder = template.getForObject(LOAD, PurchaseOrder.class, firstPurchaseOrder);
        assertEquals(newInvoiceAddress, purchaseOrder.getInvoiceAddress());
        assertEquals(2, purchaseOrder.getAllInvoiceAddresses().size());
    }

}
