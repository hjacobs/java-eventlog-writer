/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zalando.storedprocedurewrapper.proxy.executestrategies;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.typemapper.core.TypeMapperFactory;

import de.zalando.storedprocedurewrapper.proxy.ExecuteStrategy;

/**
 * @author  jmussler
 */
public class RowMapperStrategy implements ExecuteStrategy {
    @Override
    public Object executeSproc(final DataSource ds, final String sql, final Object[] args, final int[] types,
            final Class returnType) {
        return (new JdbcTemplate(ds)).query(sql, args, types, TypeMapperFactory.createTypeMapper(returnType));
    }

}
