
package de.zalando.sprocwrapper.example;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;

import de.zalando.sprocwrapper.AbstractSProcService;
import de.zalando.sprocwrapper.dsprovider.BitmapShardDataSourceProvider;

@Repository
public class ExampleBitmapShardSProcServiceImpl
    extends AbstractSProcService<ExampleBitmapShardSProcService, BitmapShardDataSourceProvider>
    implements ExampleBitmapShardSProcService {

    @Autowired
    public ExampleBitmapShardSProcServiceImpl(final BitmapShardDataSourceProvider p) {
        super(p, ExampleBitmapShardSProcService.class);
    }

    @Override
    public int getShardIndex(final int shard) {
        return sproc.getShardIndex(shard);
    }

    @Override
    public List<String> collectDataFromAllShards(final String someParameter) {
        return sproc.collectDataFromAllShards(someParameter);
    }

    @Override
    public Integer searchSomethingOnShards(final String someParameter) {
        return sproc.searchSomethingOnShards(someParameter);
    }

    @Override
    public List<Integer> searchSomethingElseOnShards(final String someParameter) {
        return sproc.searchSomethingElseOnShards(someParameter);
    }

    @Override
    public String getShardName(final int shard) {
        return sproc.getShardName(shard);
    }

    @Override
    public List<String> collectDataUsingAutoPartition(final List<String> keys) {
        return sproc.collectDataUsingAutoPartition(keys);
    }

    @Override
    public List<String> collectDataUsingAutoPartition2(final List<ExampleShardedObject> keys,
            final int additionalParam) {
        return sproc.collectDataUsingAutoPartition2(keys, additionalParam);
    }

}
