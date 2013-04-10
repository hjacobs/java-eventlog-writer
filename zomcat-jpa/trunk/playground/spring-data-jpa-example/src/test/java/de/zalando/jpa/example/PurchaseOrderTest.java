package de.zalando.jpa.example;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.transaction.annotation.Transactional;

import de.zalando.jpa.example.order.OrderStatus;
import de.zalando.jpa.example.order.PurchaseOrder;
import de.zalando.jpa.example.order.PurchaseOrderRepository;

/**
 * @author  jbellmann
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class PurchaseOrderTest {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderTest.class);

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Test
    @Rollback(false)
    public void testSavePurchaseOrder() {
        PurchaseOrder order = new PurchaseOrder();
        order.setOrderStatus(OrderStatus.ORDERED);
        order.setBrandCode("BRANDCODE_A");
        purchaseOrderRepository.save(order);

        Assert.assertNotNull(order.getBusinessKey());
        Assert.assertNotNull(order.getBrandCode());
        Assert.assertNotNull(order.getCreatedBy());
        Assert.assertNotNull(order.getModifiedBy());
        Assert.assertNotNull(order.getCreationDate());
        Assert.assertNotNull(order.getModificationDate());

        LOG.info("PurchaseOrder to save on commit : {}", order);
    }
}
