package de.zalando.jpa.eclipselink.partitioning.policies;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelModifyQuery;
import org.eclipse.persistence.sessions.server.ServerSession;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import de.zalando.jpa.eclipselink.LogSupport;

import de.zalando.sprocwrapper.sharding.ShardedObject;
import de.zalando.sprocwrapper.sharding.VirtualShardKeyStrategy;

/**
 * TODO, maybe we can generalize some bits here?
 *
 * @author  maciej
 * @author  jbellmann
 */
public class ShardedObjectPartitionPolicy extends PartitioningPolicy {

    private static final long serialVersionUID = 1L;

    private VirtualShardKeyStrategy shardingStrategy;

    private String[] dataSources;

    private int mask;

    public ShardedObjectPartitionPolicy() {
        setName("SkuSharding");
    }

// public ShardedObjectPartitionPolicy(final DefaultPersistenceUnitManager persistenceUnitManager,
// final VirtualShardKeyStrategy shardingStrategy) {
// this.shardingStrategy = shardingStrategy;
//
// final DataSourceLookup dataSourceLookup = persistenceUnitManager.getDataSourceLookup();
// Preconditions.checkArgument(dataSourceLookup instanceof MapDataSourceLookup);
//
// final Map<String, DataSource> dataSourcesMap = ((MapDataSourceLookup) dataSourceLookup).getDataSources();
//
// int value = dataSourcesMap.size();
// int maskLength = 0;
// while (value > 0) {
// maskLength++;
// value = value >> 1;
// }
//
// maskLength--;
//
// mask = (1 << maskLength) - 1;
// dataSources = new String[1 << maskLength];
//
// Preconditions.checkArgument(dataSources.length == dataSourcesMap.size(),
// "Number of data sources: %s should equal mask capacity: %s", dataSourcesMap.size(), dataSources.length);
//
// final List<String> dataSourceNames = Ordering.natural().sortedCopy(dataSourcesMap.keySet());
//
// int i = 0;
// for (final String dataSourceName : dataSourceNames) {
// dataSources[i] = dataSourceName;
// i++;
// }
//
// }

    public VirtualShardKeyStrategy getShardingStrategy() {
        return shardingStrategy;
    }

    public void setShardingStrategy(final VirtualShardKeyStrategy shardingStrategy) {
        this.shardingStrategy = shardingStrategy;
    }

    /**
     * We put the information from constructor to this 'initialize'-method.
     */
    @Override
    public void initialize(final AbstractSession session) {
        Preconditions.checkNotNull(this.shardingStrategy, "ShardingStrategy not configured");

        super.initialize(session);

        if (this.dataSources == null && session.isServerSession()) {
            Set<String> allPoolNames = ((ServerSession) session).getConnectionPools().keySet();
            Set<String> poolNames = Sets.filter(allPoolNames, new StartingWithNodeOnly());
            int value = poolNames.size();
            int maskLength = 0;
            while (value > 0) {
                maskLength++;
                value = value >> 1;
            }

            maskLength--;

            mask = (1 << maskLength) - 1;
            dataSources = new String[1 << maskLength];

            Preconditions.checkArgument(dataSources.length == poolNames.size(),
                "Number of data sources: %s should equal mask capacity: %s", poolNames.size(), dataSources.length);

            final List<String> dataSourceNames = Ordering.natural().sortedCopy(poolNames);

            int i = 0;
            for (final String dataSourceName : dataSourceNames) {
                dataSources[i] = dataSourceName;
                i++;
            }
        }
    }

    @Override
    public List<Accessor> getConnectionsForQuery(final AbstractSession session, final DatabaseQuery query,
            final AbstractRecord arguments) {

        ShardedObject shardedArgument = null;

        // yes, a query seems to modify objects, sounds a bit strange but it works
        if (ObjectLevelModifyQuery.class.isAssignableFrom(query.getClass())) {

            final Object objectToModify = ((ObjectLevelModifyQuery) query).getObject();

            if (ShardedObject.class.isAssignableFrom(objectToModify.getClass())) {

                shardedArgument = ((ShardedObject) objectToModify);
            }
        }

        LogSupport.logFine(session, "query : value {0}", shardedArgument);

// final ShardedObject shardedArgument = getFirstShardedArgument(arguments);

        if (shardedArgument == null) {
            final List<Accessor> accessors = new ArrayList<>(dataSources.length);
            for (final String poolName : this.dataSources) {
                accessors.add(getAccessor(poolName, session, query, false));
            }

            // replication, if we use null the default will be taken, what can be other than 'Replicate'
            return accessors;
        } else {
            final Object shardKey = shardedArgument.getShardKey();
            final int virtualShardId = shardingStrategy.getShardId(new Object[] {shardKey});
            final String dataSource = dataSources[virtualShardId & mask];
            LogSupport.logFine(session, "query : selected datasource-name for query {0}", dataSource);
            return Lists.newArrayList(getAccessor(dataSource, session, query, false));
        }
    }

// private ShardedObject getFirstShardedArgument(final AbstractRecord arguments) {
//
// if (arguments != null && !arguments.values().isEmpty()) {
// for (final Object arg : arguments.values()) {
// if (arg instanceof ShardedObject) {
// return (ShardedObject) arg;
// }
// }
// }
//
// return null;
// }

    /**
     * Extracts all Strings starting with 'node'.
     *
     * @author  jbellmann
     */
    private static final class StartingWithNodeOnly implements Predicate<String> {

        private static final String NODE = "node";

        @Override
        public boolean apply(final String input) {
            return input != null ? input.startsWith(NODE) : false;
        }

    }
}
