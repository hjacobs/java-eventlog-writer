package de.zalando.jpa.example;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.transaction.annotation.Transactional;

import de.zalando.jpa.config.DataSourceConfig;
import de.zalando.jpa.config.JpaConfig;

/**
 * @author  jbellmann
 */
// @Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
@ActiveProfiles(value = "HSQL")
public class PurchaseOrderTest extends AbstractPurchaseOrderTestSupport {

    @Test
    @Rollback(false)
    public void testSavePurchaseOrder() {
        super.doTestSavePurchaseOrder();
    }

    @Configuration
    @Import({ JpaConfig.class, DataSourceConfig.class })
    @EnableJpaRepositories("de.zalando.jpa.example.order")
    @ImportResource("classpath:/enableAuditing.xml")
    static class TestConfig { }

/*    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderTest.class);
 *
 *  @Autowired
 *  private PurchaseOrderRepository purchaseOrderRepository;
 *
 *  @Test
 *  @Rollback(false)
 *  public void testSavePurchaseOrder() {
 *      PurchaseOrder order = new PurchaseOrder();
 *      order.setOrderStatus(OrderStatus.ORDERED);
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
 *
 *      order.getPositions().add(new PurchaseOrderPosition(order));
 *      order.getPositions().add(new PurchaseOrderPosition(order));
 *
 *      order.setAddress(new Address());
 *
 *      purchaseOrderRepository.saveAndFlush(order);
 *      LOG.info("PurchaseOrder to save with Postions : {}", order);
 *
 *      for (PurchaseOrderPosition pos : order.getPositions()) {
 *          LOG.info("Saved Position : {}", pos.toString());
 *      }
 *
 *      LOG.info("PurchaseOrder to save with Postions : {}", order);
 *      LOG.info("Address : {}", order.getAddress());
 *
 *      LOG.info("---- NOW DELETE THE FIRST ----------");
 *      order.getPositions().remove(0);
 *      purchaseOrderRepository.saveAndFlush(order);
 *
 *      LOG.info("---- AFTER DELETE ----------");
 *      for (PurchaseOrderPosition pos : order.getPositions()) {
 *          LOG.info("Saved Position : {}", pos.toString());
 *      }
 *
 *      LOG.info("PurchaseOrder to save with Postions : {}", order);
 *      LOG.info("Address : {}", order.getAddress());
 *
 *  }
 *
 *  @Configuration
 *  @Import({ JpaConfig.class, DataSourceConfig.class })
 *  @EnableJpaRepositories("de.zalando.jpa.example.order")
 *  @ImportResource("classpath:/enableAuditing.xml")
 *  static class TestConfig { }*/
}
