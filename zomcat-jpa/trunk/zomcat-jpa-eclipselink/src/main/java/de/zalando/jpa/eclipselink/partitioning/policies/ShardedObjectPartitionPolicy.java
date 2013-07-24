package de.zalando.jpa.eclipselink.partitioning.policies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;

import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup;

import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import de.zalando.sprocwrapper.sharding.ShardedObject;
import de.zalando.sprocwrapper.sharding.VirtualShardKeyStrategy;

/**
 * TODO, maybe we can generalize some bits here?
 */
public class ShardedObjectPartitionPolicy extends PartitioningPolicy {

    private VirtualShardKeyStrategy shardingStrategy;

    private final String[] dataSources;
    private final int mask;

    public ShardedObjectPartitionPolicy(final DefaultPersistenceUnitManager persistenceUnitManager,
            final VirtualShardKeyStrategy shardingStrategy) {
        this.shardingStrategy = shardingStrategy;

        final DataSourceLookup dataSourceLookup = persistenceUnitManager.getDataSourceLookup();
        Preconditions.checkArgument(dataSourceLookup instanceof MapDataSourceLookup);

        final Map<String, DataSource> dataSourcesMap = ((MapDataSourceLookup) dataSourceLookup).getDataSources();

        int value = dataSourcesMap.size();
        int maskLength = 0;
        while (value > 0) {
            maskLength++;
            value = value >> 1;
        }

        maskLength--;

        mask = (1 << maskLength) - 1;
        dataSources = new String[1 << maskLength];

        Preconditions.checkArgument(dataSources.length == dataSourcesMap.size(),
            "Number of data sources: %s should equal mask capacity: %s", dataSourcesMap.size(), dataSources.length);

        final List<String> dataSourceNames = Ordering.natural().sortedCopy(dataSourcesMap.keySet());

        int i = 0;
        for (final String dataSourceName : dataSourceNames) {
            dataSources[i] = dataSourceName;
            i++;
        }

    }

    @Override
    public List<Accessor> getConnectionsForQuery(final AbstractSession session, final DatabaseQuery query,
            final AbstractRecord arguments) {

        final ShardedObject shardedArgument = getFirstShardedArgument(arguments);

        if (shardedArgument == null) {
            final List<Accessor> accessors = new ArrayList<>(dataSources.length);
            for (final String poolName : this.dataSources) {
                accessors.add(getAccessor(poolName, session, query, false));
            }

            return accessors;
        } else {
            final Object shardKey = shardedArgument.getShardKey();
            final int virtualShardId = shardingStrategy.getShardId(new Object[] {shardKey});
            final String dataSource = dataSources[virtualShardId & mask];
            return Lists.newArrayList(getAccessor(dataSource, session, query, false));
        }
    }

    private ShardedObject getFirstShardedArgument(final AbstractRecord arguments) {

        if (arguments != null && !arguments.values().isEmpty()) {
            for (final Object arg : arguments.values()) {
                if (arg instanceof ShardedObject) {
                    return (ShardedObject) arg;
                }
            }
        }

        return null;
    }

}
