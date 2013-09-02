package de.zalando.jpa.example.sharding.context;

/**
 * @author  jbellmann
 */
public class ShardKeyContextHolder {

    private static ShardKeyContextHolderStrategy strategy = new ThreadLocalContextHolderStrategy();

    public static void clearContext() {

        strategy.clearContext();
    }

    public static ShardKeyContext getContext() {

        return strategy.getContext();
    }

    public static void setContext(final ShardKeyContext shardKeyContext) {

        strategy.setContext(shardKeyContext);
    }

    public static ShardKeyContext createEmptyContext() {

        return strategy.createEmptyContext();
    }

    public static ShardKeyContextHolderStrategy getStrategy() {

        return strategy;
    }

    public static void setShardKeyContextHolderStrategy(
            final ShardKeyContextHolderStrategy shardKeyHolderContextStrategy) {
        ShardKeyContextHolder.strategy = shardKeyHolderContextStrategy;
    }
}
