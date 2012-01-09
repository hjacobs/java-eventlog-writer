package de.zalando.sprocwrapper.proxy;

/**
 * @author  jmussler
 */
public class ShardKeyParameter {
    public int javaPos;
    public int keyPos;

    public ShardKeyParameter(final int j, final int k) {
        javaPos = j;
        keyPos = k;
    }
}
