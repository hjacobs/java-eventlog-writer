package de.zalando.jpa.example;

import org.junit.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import de.zalando.jpa.example.order.Address;
import de.zalando.jpa.example.order.OrderStatus;
import de.zalando.jpa.example.order.PurchaseOrder;
import de.zalando.jpa.example.order.PurchaseOrderPosition;
import de.zalando.jpa.example.order.PurchaseOrderRepository;

/**
 * The testcode for integration an unit-test.
 *
 * @author  jbellmann
 */
public abstract class AbstractPurchaseOrderTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPurchaseOrderTestSupport.class);

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    public void doTestSavePurchaseOrder() {
        PurchaseOrder order = new PurchaseOrder();
        order.setOrderStatus(OrderStatus.ORDERED);
        order.setBrandCode("BRANDCODE_A");
        purchaseOrderRepository.saveAndFlush(order);

        Assert.assertNotNull(order.getBusinessKey());
        Assert.assertNotNull(order.getBrandCode());
        Assert.assertNotNull(order.getCreatedBy());
        Assert.assertNotNull(order.getModifiedBy());
        Assert.assertNotNull(order.getCreationDate());
        Assert.assertNotNull(order.getModificationDate());

        LOG.info("PurchaseOrder to save on commit : {}", order);

        order.getPositions().add(new PurchaseOrderPosition(order));
        order.getPositions().add(new PurchaseOrderPosition(order));

        order.setAddress(new Address());

        purchaseOrderRepository.saveAndFlush(order);
        LOG.info("PurchaseOrder to save with Postions : {}", order);

        for (PurchaseOrderPosition pos : order.getPositions()) {
            LOG.info("Saved Position : {}", pos.toString());
        }

        LOG.info("PurchaseOrder to save with Postions : {}", order);
        LOG.info("Address : {}", order.getAddress());

        LOG.info("---- NOW DELETE THE FIRST ----------");
        order.getPositions().remove(0);
        purchaseOrderRepository.saveAndFlush(order);

        LOG.info("---- AFTER DELETE ----------");
        for (PurchaseOrderPosition pos : order.getPositions()) {
            LOG.info("Saved Position : {}", pos.toString());
        }

        LOG.info("PurchaseOrder to save with Postions : {}", order);
        LOG.info("Address : {}", order.getAddress());

    }

}
