package de.zalando.jpa.example.sharding.context;

import de.zalando.jpa.config.ShardKey;

/**
 * Defines an Context for an {@link ShardKey}.
 *
 * @author  jbellmann
 */
public interface ShardKeyContext {

    void setShardKey(ShardKey shardKey);

    ShardKey getShardKey();

}
