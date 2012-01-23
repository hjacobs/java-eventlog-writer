package de.zalando.sprocwrapper.dsprovider;

import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

/**
 * @author  jmussler
 */
public class StockServiceDataSourceProvider implements DataSourceProvider {
    private DataSource[] dss = new DataSource[8];

    public StockServiceDataSourceProvider(final Map<Integer, DataSource> dzz) {
        for (Entry<Integer, DataSource> e : dzz.entrySet()) {
            dss[e.getKey()] = e.getValue();
        }
    }

    /**
     * Letzten 3 Bit bestimmen 8 Shards.
     */
    @Override
    public DataSource getDataSource(final int virtualShardId) {
        return dss[virtualShardId & (1 + 2 + 4)];
    }
}
