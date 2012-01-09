package de.zalando.storedprocedurewrapper;

/**
 * @author  jmussler
 */
public class VirtualShardIdFromStringStrategy extends VirtualShardKeyStrategy {

    @Override
    public int getShardId(final Object[] objs) {
        return 1; // return md5((String)objs[0]);
    }
}
