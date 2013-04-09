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
        LOG.info("Found and return PurchaseOrder: {}", po.toString());
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {

            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // to show the lazy loaded InvoiceAddress
        InvoiceAddress a = po.getInvoiceAddress();
        LOG.info(a.toString());
        return po;
    }

}
