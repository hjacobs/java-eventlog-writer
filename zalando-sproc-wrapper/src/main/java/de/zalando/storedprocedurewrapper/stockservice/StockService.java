
package de.zalando.storedprocedurewrapper.stockservice;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;

import de.zalando.storedprocedurewrapper.AbstractSprocService;
import de.zalando.storedprocedurewrapper.ArrayDataSourceProvider;

/**
 * @author  jmussler
 */
@Repository
public class StockService extends AbstractSprocService<StockServiceInterface, ArrayDataSourceProvider>
    implements StockServiceInterface {

    @Autowired
    public StockService(final ArrayDataSourceProvider p) {
        super(p, StockServiceInterface.class);
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

    public Integer getOtherInt() {

        return service.getOtherInt();
    }

    public Integer getSelectValue(final int i) {
        return service.getSelectValue(i);
    }

    public List<TestResult> getResult() {
        return service.getResult();
    }

    public TestResult getSingleResult() {
        return service.getSingleResult();
    }

    public Integer getBla() {
        return service.getBla();
    }

    public String getDatabase(final int i) {
        return service.getDatabase(i);
    }
}
