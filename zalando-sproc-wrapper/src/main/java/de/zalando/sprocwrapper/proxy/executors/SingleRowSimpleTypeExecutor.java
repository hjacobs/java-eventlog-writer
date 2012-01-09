/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zalando.sprocwrapper.proxy.executors;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author  jmussler
 */
public class SingleRowSimpleTypeExecutor implements Executor {

    public Object executeSProc(final DataSource ds, final String sql, final Object[] args, final int[] types,
            final Class returnType) {
        return (new JdbcTemplate(ds)).queryForObject(sql, args, types, returnType);
    }
}
