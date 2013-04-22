package de.zalando.data.jpa;

import java.util.List;

import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.client.RestTemplate;

import de.zalando.data.jpa.domain.PurchaseOrder;

/**
 * Integration-Test to show LAZY_LOADING.
 *
 * @author  jbellmann
 */
public class PurchaseOrderServiceIT {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderServiceIT.class);

    private static final String URL_ALL = "http://localhost:9092/static-weaving/purchaseorders/all";
    private static final String LOAD_WITH_WAIT_TIME =
        "http://localhost:9092/static-weaving/purchaseorders/{businesskey}/{timeinMillis}";

    private Integer timeInMillis = 1500;

    @Test
    public void test() {

        RestTemplate template = new RestTemplate();
        List<?> result = template.getForObject(URL_ALL, List.class);
        for (Object key : result) {
            LOG.info("GOT KEY : " + key);

            PurchaseOrder order = template.getForObject(LOAD_WITH_WAIT_TIME, PurchaseOrder.class, key, timeInMillis);
            LOG.info("GOT PO ON CLIENT : {}", order);
        }
    }

}
