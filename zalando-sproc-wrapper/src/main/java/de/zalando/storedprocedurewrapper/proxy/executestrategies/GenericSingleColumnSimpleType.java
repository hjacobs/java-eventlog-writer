/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zalando.storedprocedurewrapper.proxy.executestrategies;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import de.zalando.storedprocedurewrapper.proxy.ExecuteStrategy;

/**
 * @author  jmussler
 */
public class GenericSingleColumnSimpleType implements ExecuteStrategy {

    public Object executeSproc(final DataSource ds, final String sql, final Object[] args, final int[] types,
            final Class returnType) {
        return (new JdbcTemplate(ds)).queryForObject(sql, args, types, returnType);
    }
}
