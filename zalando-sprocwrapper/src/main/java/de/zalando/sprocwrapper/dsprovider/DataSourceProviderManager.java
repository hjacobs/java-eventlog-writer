package de.zalando.sprocwrapper.dsprovider;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.google.common.base.Preconditions;

import de.zalando.zomcat.flowid.FlowPriority;
import de.zalando.zomcat.flowid.FlowPriority.Priority;

public class DataSourceProviderManager implements DataSourceProvider {

    private final Map<Priority, DataSourceProvider> dataSourceProviders;

    public DataSourceProviderManager(final Map<Priority, DataSourceProvider> dataSourceProviders) {
        this.dataSourceProviders = dataSourceProviders;

        // do some checks on the given datasource providers:
        Preconditions.checkNotNull(dataSourceProviders, "DataSourceProviders is null - wrong spring configuration.");
        Preconditions.checkNotNull(dataSourceProviders.get(Priority.DEFAULT),
            "No default datasource provider given: [{}]" + dataSourceProviders);

        if (dataSourceProviders.size() > 1) {

            // we have multiple datasources defined. check that they have the same size and shardIds:
            List<Integer> distinctShardIds = null;
            for (final DataSourceProvider dataSourceProvider : dataSourceProviders.values()) {
                if (distinctShardIds == null) {
                    distinctShardIds = dataSourceProvider.getDistinctShardIds();
                    Preconditions.checkNotNull(distinctShardIds,
                        "distinctShardIds is null for dataSourceProvider [{}] ", dataSourceProvider);
                } else {
                    Preconditions.checkArgument(distinctShardIds.equals(dataSourceProvider.getDistinctShardIds()),
                        "distinctShardIds are not equal [{}]", dataSourceProviders);
                }
            }
        }
    }

    @Override
    public DataSource getDataSource(final int virtualShardId) {
        return getDataSourceProvider().getDataSource(virtualShardId);
    }

    @Override
    public List<Integer> getDistinctShardIds() {
        return getDataSourceProvider().getDistinctShardIds();
    }

    private DataSourceProvider getDataSourceProvider() {
        final Priority flowPriority = FlowPriority.flowPriority();
        return Preconditions.checkNotNull(dataSourceProviders.get(flowPriority),
                "No dataSourceProvider in DataSourceProviderManager defined for flowPriority [{}]", flowPriority);
    }

}
