package de.zalando.data.jpa.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;

import de.zalando.data.jpa.domain.InvoiceAddress;
import de.zalando.data.jpa.domain.PurchaseOrder;
import de.zalando.data.jpa.repository.PurchaseOrderRepository;

/**
 * @author  jbellmann
 */
@Controller
@RequestMapping(value = "/purchaseorders", produces = {})
public class PurchaseOrderController {

    private static final Logger LOG = LoggerFactory.getLogger(PurchaseOrderController.class);

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @RequestMapping(value = "/{businesskey}", method = RequestMethod.GET)
    @ResponseBody
    public PurchaseOrder loadPurchaseOrder(@PathVariable final String businesskey) {
        PurchaseOrder po = this.purchaseOrderRepository.findByBusinessKey(businesskey);
        LOG.info("Found and return PurchaseOrder: {}", po.toString());
        return po;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public List<PurchaseOrder> loadPurchaseOrders() {
        List<PurchaseOrder> all = this.purchaseOrderRepository.findAll();
        return all;
    }

    @RequestMapping(value = "/{businesskey}/{time}", method = RequestMethod.GET)
    @ResponseBody
    public PurchaseOrder loadPurchaseOrderWithPause(@PathVariable final String businesskey,
            @PathVariable final int time) {
        PurchaseOrder po = this.purchaseOrderRepository.findByBusinessKey(businesskey);
        LOG.info("Found PurchaseOrder: {}", po.toString());
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {

            LOG.error(e.getMessage(), e);
        }

        // to show the lazy loaded InvoiceAddress
        LOG.info("Now access corresponding InvoiceAddress. When LAZY-LOADING WORKS you should see an SELECT.");

        InvoiceAddress a = po.getInvoiceAddress();
        LOG.info("InvoiceAddress loaded : {} ", a.toString());
        LOG.info("Next SELECT should come from marshalling to JSON");
        return po;
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ResponseBody
    public List<String> loadPurchaseOrderBusinessKey() {

        // there are only 10, so we can load all
        List<PurchaseOrder> allPurchaseOrders = this.purchaseOrderRepository.findAll();
        List<String> result = Lists.newArrayList();
        for (PurchaseOrder order : allPurchaseOrders) {
            result.add(order.getBusinessKey());
        }

        return result;
    }

}
