package de.zalando.sprocwrapper.sharding;

public class VirtualShardIdentityStrategy extends VirtualShardKeyStrategy {

    /**
     * @param   objs  Key Objects
     *
     * @return  virtual shard id
     */
    public int getShardId(final Object[] objs) {
        return (Integer) objs[0];
    }
}
