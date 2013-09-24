package de.zalando.data.jpa.domain.support.jdbc;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;

import org.springframework.util.Assert;

import de.zalando.data.jpa.domain.support.BusinessKeyGenerator;

/**
 * Eine alternative zur SprocService-Implementierung. Klein, einfach und gut zu verstehen.
 *
 * @author      jbellmann
 * @deprecated  this wrapper is not necessary here, use {@link StoredProcedureBusinessKeyGenerator}
 */
@Deprecated
public class SpringJdbcBusinessKeyGenerator implements BusinessKeyGenerator, InitializingBean {

    protected DataSource dataSource;

    private StoredProcedureBusinessKeyGenerator procedure;

    public void afterPropertiesSet() {
        Assert.notNull(dataSource, "DataSource should never be null");
        procedure = new StoredProcedureBusinessKeyGenerator(dataSource);

        // TODO koennen wir hier je start eine Nummer verbraten? oder muss das hier lueckenlos sein?
        // if this test is fails, something went wrong and system should halt until fixed
        // String initialTest = procedure.nextNumber(NumberRangeType.PURCHASE_ORDER);
        // Assert.notNull(initialTest, "Init-Test failed");
    }

    @Override
    public String getBusinessKeyForSelector(final String businessKeySelector) {
        Assert.notNull(businessKeySelector, "BusinessKeySelector should not be null");
        return procedure.getBusinessKeyForSelector(businessKeySelector);
    }

    public void setDataSource(final DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource has to be not null");
        this.dataSource = dataSource;
    }

}
