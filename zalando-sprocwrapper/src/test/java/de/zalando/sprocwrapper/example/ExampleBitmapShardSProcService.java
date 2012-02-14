package de.zalando.sprocwrapper.example;

import java.util.List;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;
import de.zalando.sprocwrapper.sharding.ShardKey;
import de.zalando.sprocwrapper.sharding.VirtualShardIdentityStrategy;
import de.zalando.sprocwrapper.sharding.VirtualShardMd5Strategy;

/**
 * @author  jmussler
 */
@SProcService
public interface ExampleBitmapShardSProcService {

    @SProcCall(shardStrategy = VirtualShardIdentityStrategy.class)
    int getShardIndex(@ShardKey int shard);

    @SProcCall(runOnAllShards = true)
    List<String> collectDataFromAllShards(@SProcParam String someParameter);

    @SProcCall(searchShards = true)
    Integer searchSomethingOnShards(@SProcParam String someParameter);

    @SProcCall(shardStrategy = VirtualShardIdentityStrategy.class, sql = "SELECT shard_name FROM shard_name")
    String getShardName(@ShardKey int shard);

    @SProcCall(shardStrategy = VirtualShardMd5Strategy.class)
    List<String> collectDataUsingAutoPartition(@ShardKey @SProcParam List<String> keys);

    @SProcCall(shardStrategy = VirtualShardMd5Strategy.class, name = "collect_data_using_auto_partition2")
    List<String> collectDataUsingAutoPartition2(
            @ShardKey
            @SProcParam(type = "example_sharded_object[]")
            List<ExampleShardedObject> keys, @SProcParam int additionalParam);

}
