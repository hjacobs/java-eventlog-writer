
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

    public long getSimpleLong() {

        return sproc.getSimpleLong();
    }

    public int getSimpleInt(final int i) {
        return sproc.getSimpleInt(i);
    }

    public boolean getBoolean() {
        return sproc.getBoolean();
    }

    public void getSimpleIntVoid(final int i) {
        sproc.getSimpleIntVoid(i);
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

    @Override
    public String createOrUpdateMultipleObjectsWithMap(final List<ExampleDomainObjectWithMap> objects) {
        return sproc.createOrUpdateMultipleObjectsWithMap(objects);
    }

    @Override
    public String createOrUpdateMultipleObjectsWithInnerObject(final List<ExampleDomainObjectWithInnerObject> objects) {
        return sproc.createOrUpdateMultipleObjectsWithInnerObject(objects);
    }

    @Override
    public void createOrUpdateMultipleObjectsWithMapVoid(final List<ExampleDomainObjectWithMap> objects) {
        sproc.createOrUpdateMultipleObjectsWithMapVoid(objects);
    }

    @Override
    public boolean reserveStock(final String sku) {
        return sproc.reserveStock(sku);
    }

}
