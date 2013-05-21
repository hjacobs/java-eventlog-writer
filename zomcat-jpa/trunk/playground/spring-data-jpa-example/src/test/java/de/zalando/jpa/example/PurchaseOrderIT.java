package de.zalando.jpa.example;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.transaction.annotation.Transactional;

import de.zalando.jpa.config.DataSourceConfig;
import de.zalando.jpa.config.JpaConfig;

/**
 * @author  jbellmann
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
@ActiveProfiles("POSTGRES")
public class PurchaseOrderIT extends AbstractPurchaseOrderTestSupport {

    @Test
// @Rollback(false)
    public void testSavePurchaseOrder() {
        super.doTestSavePurchaseOrder();
    }

    @Configuration
    @Import({ JpaConfig.class, DataSourceConfig.class })
    @EnableJpaRepositories("de.zalando.jpa.example.order")
    @ImportResource("classpath:/enableAuditing.xml")
    static class TestConfig { }

/*    @Test
 * // @Rollback(false)
 *  public void testSavePurchaseOrder() {
 *      PurchaseOrder order = new PurchaseOrder();
 *      order.setOrderStatus(OrderStatus.INITIAL);
 *      order.setBrandCode("BRANDCODE_A");
 *      purchaseOrderRepository.saveAndFlush(order);
 *
 *      Assert.assertNotNull(order.getBusinessKey());
 *      Assert.assertNotNull(order.getBrandCode());
 *      Assert.assertNotNull(order.getCreatedBy());
 *      Assert.assertNotNull(order.getModifiedBy());
 *      Assert.assertNotNull(order.getCreationDate());
 *      Assert.assertNotNull(order.getModificationDate());
 *
 *      LOG.info("PurchaseOrder to save on commit : {}", order);
 *  }*/
}
