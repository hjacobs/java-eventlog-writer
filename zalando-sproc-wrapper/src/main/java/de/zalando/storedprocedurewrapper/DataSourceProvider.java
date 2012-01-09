package de.zalando.storedprocedurewrapper;

import javax.sql.DataSource;

/**
 * @author  jmussler
 */
public interface DataSourceProvider {
    DataSource getDataSource(int id);
}
