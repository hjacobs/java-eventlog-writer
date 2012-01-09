/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zalando.storedprocedurewrapper;

/**
 * @author  jmussler
 */
public class VirtualShardIdFromLong extends VirtualShardKeyStrategy {

    @Override
    public int getShardId(final Object[] objs) {
        return (((Integer) objs[0]) % 2); // example only
    }
}
