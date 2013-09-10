package de.zalando.data.jpa.domain.support.jdbc;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;

import org.springframework.jdbc.support.incrementer.AbstractSequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.H2SequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.HsqlSequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.PostgreSQLSequenceMaxValueIncrementer;

import org.springframework.util.Assert;

import com.google.common.collect.Maps;

import de.zalando.data.jpa.domain.support.SeqIdGenerator;

/**
 * Author: clohmann Date: 06.05.13 Time: 17:28
 */
public class SequenceIdGenerator implements InitializingBean, SeqIdGenerator {

    public static final String HSQL_DATABASE_PLATFORM = "HSQL";
    public static final String POSTGRES_DATABASE_PLATFORM = "POSTGRES";
    public static final String H2_DATABASE_PLATFORM = "H2";

    private DataSource dataSource;

    private String databasePlatform = POSTGRES_DATABASE_PLATFORM;

    private final Object lock = new Object();

    static final Map<String, AbstractSequenceMaxValueIncrementer> INCREMENTER_CACHE = Maps.newConcurrentMap();

    public SequenceIdGenerator(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public SequenceIdGenerator() { }

    @Override
    public Number getSeqId(final String sequenceName, final boolean negateSku) {
        long nextValue = getIncrementerFromCache(sequenceName).nextLongValue();
        if (negateSku) {

            return nextValue * (-1L);
        } else {

            return nextValue;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(dataSource, "dataSource should never be null");

    }

    protected AbstractSequenceMaxValueIncrementer getIncrementerFromCache(final String sequenceName) {
        AbstractSequenceMaxValueIncrementer result = INCREMENTER_CACHE.get(sequenceName);

        if (result == null) {

            // is there really no incrementer
            synchronized (lock) {
                result = INCREMENTER_CACHE.get(sequenceName);
                if (result == null) {
                    result = getMaxValueIncrementer();
                    result.setDataSource(dataSource);
                    result.setIncrementerName(sequenceName);
                    result.afterPropertiesSet();
                    INCREMENTER_CACHE.put(sequenceName, result);
                }
            }
        }

        return result;

    }

    protected AbstractSequenceMaxValueIncrementer getMaxValueIncrementer() {
        if (POSTGRES_DATABASE_PLATFORM.equals(databasePlatform)) {
            return new PostgreSQLSequenceMaxValueIncrementer();
        } else if (HSQL_DATABASE_PLATFORM.equals(databasePlatform)) {
            return new HsqlSequenceMaxValueIncrementer();
        } else if (H2_DATABASE_PLATFORM.equals(databasePlatform)) {
            return new H2SequenceMaxValueIncrementer();
        }

        throw new RuntimeException("No SequenceMaxValueIncrementer found for 'databasePlatform' : " + databasePlatform);
    }

    public void setDataSource(final DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource should never be null");
        this.dataSource = dataSource;
    }

    public void setDatabasePlatform(final String databasePlatform) {
        Assert.hasText(databasePlatform, "DatabasePlatform should never be null or empty");
        this.databasePlatform = databasePlatform;
    }

}
