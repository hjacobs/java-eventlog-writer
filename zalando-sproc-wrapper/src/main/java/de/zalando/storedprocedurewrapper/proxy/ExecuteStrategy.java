package de.zalando.storedprocedurewrapper.proxy;

import javax.sql.DataSource;

/**
 * @author  jmussler
 */
public interface ExecuteStrategy {
    Object executeSproc(DataSource ds, String sql, Object[] args, int[] types, Class returnType);
}
