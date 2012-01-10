
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
        sproc.createArticleSimple(sku);
    }

    public String createArticleSimpleItems(final String sku, final int stockId, final int quantity,
            final int purchasePrice, final String referenceNumber) {
        if (sku == null) {
            throw new IllegalArgumentException("SKU");
        }

        return sproc.createArticleSimpleItems(sku, stockId, quantity, purchasePrice, referenceNumber);
    }

    public Integer getSimpleInt() {
        return sproc.getSimpleInt();
    }

    public int getSimpleIntAsPrimitive() {
        return sproc.getSimpleIntAsPrimitive();
    }

    public void getSimpleIntIgnore() {
        sproc.getSimpleInt();
    }

    public long getSimpleLong() {

        return sproc.getSimpleLong();
    }

    public int getSimpleInt(final int i) {
        return sproc.getSimpleInt(i);
    }

    public List<ExampleDomainObject> getResult() {
        return sproc.getResult();
    }

    public ExampleDomainObject getSingleResult() {
        return sproc.getSingleResult();
    }

    public Integer getBla() {
        return sproc.getBla();
    }

    public int getShardIndex(final int shard) {
        return sproc.getShardIndex(shard);
    }

    @Override
    public String createOrUpdateObject(final ExampleDomainObject object) {
        return sproc.createOrUpdateObject(object);
    }

    @Override
    public String createOrUpdateMultipleObjects(final List<ExampleDomainObject> objects) {
        return sproc.createOrUpdateMultipleObjects(objects);
    }

}
