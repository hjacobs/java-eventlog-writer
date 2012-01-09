package de.zalando.sprocwrapper.dsprovider;

import javax.sql.DataSource;

/**
 * @author  jmussler
 */
public interface DataSourceProvider {
    DataSource getDataSource(int id);
}
