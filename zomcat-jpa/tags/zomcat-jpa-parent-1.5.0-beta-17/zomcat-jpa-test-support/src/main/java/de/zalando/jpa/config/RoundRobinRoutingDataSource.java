package de.zalando.jpa.config;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.google.common.collect.Iterables;

/**
 * @author  jbellmann
 */
public class RoundRobinRoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger LOG = LoggerFactory.getLogger(RoundRobinRoutingDataSource.class);

    private final Iterator<ShardKey> iterableKeys = Iterables.cycle(ShardKey.ONE, ShardKey.TWO).iterator();

    @Override
    protected Object determineCurrentLookupKey() {
        ShardKey shardKey = iterableKeys.next();
        LOG.info("RETURNING SHARDKEY : {}", shardKey);
        return shardKey;
    }

}
