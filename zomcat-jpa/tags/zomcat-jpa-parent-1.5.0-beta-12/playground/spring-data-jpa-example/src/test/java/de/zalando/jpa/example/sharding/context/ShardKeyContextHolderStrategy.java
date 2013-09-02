package de.zalando.jpa.example.sharding.context;

public interface ShardKeyContextHolderStrategy {

    void clearContext();

    ShardKeyContext getContext();

    void setContext(ShardKeyContext shardKeyContext);

    ShardKeyContext createEmptyContext();

}
