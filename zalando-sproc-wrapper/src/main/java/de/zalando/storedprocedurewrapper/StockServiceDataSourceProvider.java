/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zalando.storedprocedurewrapper;

import javax.sql.DataSource;

/**
 * @author  jmussler
 */
public class StockServiceDataSourceProvider implements DataSourceProvider {

    private final DataSource ds;

    public StockServiceDataSourceProvider(final DataSource d) {
        ds = d;
    }

    public DataSource getDataSource(final int i) {
        return ds;
    }
}
