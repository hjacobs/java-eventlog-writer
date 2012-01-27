
package de.zalando.sprocwrapper.example;

import java.util.Date;
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

    @Override
    public void createArticleSimples(final List<String> skus) {
        sproc.createArticleSimples(skus);
    }

    @Override
    public String createArticleSimpleItems(final String sku, final int stockId, final int quantity,
            final int purchasePrice, final String referenceNumber) {
        if (sku == null) {
            throw new IllegalArgumentException("SKU");
        }

        return sproc.createArticleSimpleItems(sku, stockId, quantity, purchasePrice, referenceNumber);
    }

    @Override
    public Integer getSimpleInt() {
        return sproc.getSimpleInt();
    }

    @Override
    public int getSimpleIntAsPrimitive() {
        return sproc.getSimpleIntAsPrimitive();
    }

    @Override
    public long getSimpleLong() {

        return sproc.getSimpleLong();
    }

    @Override
    public int getSimpleInt(final int i) {
        return sproc.getSimpleInt(i);
    }

    @Override
    public boolean getBoolean() {
        return sproc.getBoolean();
    }

    @Override
    public void setBoolean(final boolean bool) {
        sproc.setBoolean(bool);
    }

    @Override
    public void useEnumParam(final ExampleEnum param) {
        sproc.useEnumParam(param);
    }

    @Override
    public void useCharParam(final char c) {
        sproc.useCharParam(c);
    }

    @Override
    public void useDateParam(final Date d) {
        sproc.useDateParam(d);
    }

    @Override
    public void useDateParam2(final Date d) {
        sproc.useDateParam2(d);
    }

    @Override
    public void getSimpleIntVoid(final int i) {
        sproc.getSimpleIntVoid(i);
    }

    @Override
    public boolean login(final String userName, final String password) {
        return sproc.login(userName, password);
    }

    @Override
    public List<ExampleDomainObject> getResult() {
        return sproc.getResult();
    }

    @Override
    public ExampleDomainObject getSingleResult() {
        return sproc.getSingleResult();
    }

    @Override
    public Integer getBla() {
        return sproc.getBla();
    }

    @Override
    public int getShardIndex(final int shard) {
        return sproc.getShardIndex(shard);
    }

    @Override
    public List<String> collectDataFromAllShards(final String someParameter) {
        return sproc.collectDataFromAllShards(someParameter);
    }

    @Override
    public List<Integer> getInts() {
        return sproc.getInts();
    }

    @Override
    public List<Long> getLongs() {
        return sproc.getLongs();
    }

    @Override
    public String createOrUpdateObject(final ExampleDomainObject object) {
        return sproc.createOrUpdateObject(object);
    }

    @Override
    public String createOrUpdateObjectWithEnum(final ExampleDomainObjectWithEnum object) {
        return sproc.createOrUpdateObjectWithEnum(object);
    }

    @Override
    public String createOrUpdateObjectWithDate(final ExampleDomainObjectWithDate object) {
        return sproc.createOrUpdateObjectWithDate(object);
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

    @Override
    public void createArticleSimple(final String sku) {
        sproc.createArticleSimple(sku);
    }

    @Override
    public AddressPojo createAddress(final AddressPojo a) {
        return sproc.createAddress(a);
    }

    @Override
    public AddressPojo getAddress(final AddressPojo a) {
        return sproc.getAddress(a);
    }
}
