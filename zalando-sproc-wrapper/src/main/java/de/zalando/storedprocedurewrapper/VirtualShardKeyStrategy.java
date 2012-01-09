package de.zalando.storedprocedurewrapper;

/**
 * @author  jmussler
 */
public class VirtualShardKeyStrategy {

    /**
     * @param   objs  Key Objects
     *
     * @return  virtual shard id
     */
    public int getShardId(final Object[] objs) {
        return 0;
    }
}
