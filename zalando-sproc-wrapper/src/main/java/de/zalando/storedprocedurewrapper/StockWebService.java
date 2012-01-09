package de.zalando.storedprocedurewrapper;

import javax.jws.WebMethod;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Repository;

import de.zalando.storedprocedurewrapper.proxy.SprocProxyBuilder;

/**
 * @author  jmussler
 */
@Repository
public class StockWebService {

    StockServiceInterface service = null;

    @Autowired
    public StockWebService(@Qualifier("frontenddatasource") final DataSource datasource) {
        service = SprocProxyBuilder.build(null, StockServiceInterface.class);
    }

    @WebMethod
    public void createArticleSimple(final String sku) {

        service.createArticleSimple(sku);
    }

    @WebMethod
    public void CreateArticleSimpleItems(final String sku, final int stockId, final int quantity, final int price,
            final String referenceNumber) {
        service.createArticleSimpleItems(sku, stockId, quantity, price, referenceNumber);
    }
}
