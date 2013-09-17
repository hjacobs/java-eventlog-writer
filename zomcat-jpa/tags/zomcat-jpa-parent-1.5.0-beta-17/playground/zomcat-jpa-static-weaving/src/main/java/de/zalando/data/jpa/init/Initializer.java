package de.zalando.data.jpa.init;

import static com.google.common.collect.Lists.newArrayList;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import de.zalando.data.jpa.domain.InvoiceAddress;
import de.zalando.data.jpa.domain.PurchaseOrder;
import de.zalando.data.jpa.domain.PurchaseOrderPosition;
import de.zalando.data.jpa.repository.PurchaseOrderRepository;

/**
 * @author  jbellmann
 */
@Component
public class Initializer {

    private static final Logger LOG = LoggerFactory.getLogger(Initializer.class);

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @PostConstruct
    @Transactional
    public void init() {

        for (int i = 0; i < 10; i++) {

            PurchaseOrder po = new PurchaseOrder();
            InvoiceAddress a = new InvoiceAddress();
            a.setCity("Berlin");
            a.setFirstname("Klaus");
            a.setLastname("Tester");
            a.setPostcode("11990");
            a.setStreet("Test-Strasse 11");
            po.setInvoiceAddress(a);
            po.setAllInvoiceAddresses(newArrayList(a));

            for (int j = 0; j < 3; j++) {
                PurchaseOrderPosition pos = new PurchaseOrderPosition();
                pos.setProductNumber("PO-PN0000" + j);
                pos.setQuantity(j);
                po.getPositions().add(pos);
            }

            this.purchaseOrderRepository.saveAndFlush(po);
            LOG.info("PurchaseOrder saved : {}", po);
        }

    }
}
