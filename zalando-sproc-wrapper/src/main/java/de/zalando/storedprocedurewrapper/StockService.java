
package de.zalando.storedprocedurewrapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;

/**
 * @author  jmussler
 */
@Repository
public class StockService extends AbstractSprocService<StockServiceInterface, StockServiceDataSourceProvider>
    implements StockServiceInterface {

    @Autowired
    public StockService(final StockServiceDataSourceProvider p) {
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
}
