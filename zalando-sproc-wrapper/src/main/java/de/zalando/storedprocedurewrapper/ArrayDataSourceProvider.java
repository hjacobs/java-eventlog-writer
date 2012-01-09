/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zalando.storedprocedurewrapper;

import javax.sql.DataSource;

/**
 * @author  jmussler
 */
public class ArrayDataSourceProvider implements DataSourceProvider {
    private DataSource[] dss;

    public ArrayDataSourceProvider(final DataSource[] ds) {
        dss = ds;
    }

    public DataSource getDataSource(final int id) {
        return dss[id];
    }

}
