
package de.zalando.sprocwrapper.example;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;

import de.zalando.sprocwrapper.AbstractSProcService;
import de.zalando.sprocwrapper.dsprovider.ArrayDataSourceProvider;

/**
 * @author  jmussler
 */
@Repository
public class ExampleSProcServiceImpl extends AbstractSProcService<ExampleSProcService, ArrayDataSourceProvider>
    implements ExampleSProcService {

    @Autowired
    public ExampleSProcServiceImpl(final ArrayDataSourceProvider p) {
        super(p, ExampleSProcService.class);
    }

    public void createArticleSimple(final String sku) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String createArticleSimpleItems(final String sku, final int stockId, final int quantity,
            final int purchasePrice, final String referenceNumber) {
        if (sku == null) {
            throw new IllegalArgumentException("SKU");
        }

        return service.createArticleSimpleItems(sku, stockId, quantity, purchasePrice, referenceNumber);
    }

    public Integer getSimpleInt() {
        return service.getSimpleInt();
    }

    public void getSimpleIntIgnore() {
        service.getSimpleInt();
    }

    public Integer getOtherInt() {

        return service.getOtherInt();
    }

    public Integer getSelectValue(final int i) {
        return service.getSelectValue(i);
    }

    public List<ExampleResult> getResult() {
        return service.getResult();
    }

    public ExampleResult getSingleResult() {
        return service.getSingleResult();
    }

    public Integer getBla() {
        return service.getBla();
    }

    public String getDatabase(final int i) {
        return service.getDatabase(i);
    }
}
