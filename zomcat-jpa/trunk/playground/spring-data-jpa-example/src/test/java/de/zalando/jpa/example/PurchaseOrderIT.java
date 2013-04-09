package de.zalando.jpa.example;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.transaction.annotation.Transactional;

import de.zalando.jpa.example.order.PurchaseOrderRepository;

/**
 * @author  jbellmann
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public class PurchaseOrderIT {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderIT.class);

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Test
// @Ignore
// @Rollback(false)
    public void testSavePurchaseOrder() {
// PurchaseOrder order = new PurchaseOrder();
// order.setOrderStatus(OrderStatus.INITIAL);
// order.setBrandCode("BRANDCODE_A");
// purchaseOrderRepository.save(order);
//
// Assert.assertNotNull(order.getBusinessKey());
// Assert.assertNotNull(order.getBrandCode());
// Assert.assertNotNull(order.getCreatedBy());
// Assert.assertNotNull(order.getModifiedBy());
// Assert.assertNotNull(order.getCreationDate());
// Assert.assertNotNull(order.getModificationDate());
//
// LOG.info("PurchaseOrder to save on commit : {}", order);
    }
}
