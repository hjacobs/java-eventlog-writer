package de.zalando.storedprocedurewrapper;

/**
 * @author  jmussler
 */
public class VirtualShardKeyStrategy {
    public int getShardId(final Object[] objs) {
        return 1;
    }
}
