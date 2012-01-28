package de.zalando.sprocwrapper.sharding;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author  henning
 */
public class ShardingStrategiesTest {

    @Test
    public void testVirtualShardKeyFromStringUsingMd5() {
        VirtualShardKeyStrategy strategy = new VirtualShardKeyFromStringUsingMd5();
        Assert.assertEquals(0, strategy.getShardId(null));
        Assert.assertEquals(0, strategy.getShardId(new Object[0]));
        Assert.assertEquals(0, strategy.getShardId(new Object[1]));

        Object[] args = new Object[1];
        args[0] = "A";

        // MD5("A") => 7fc56270e7a70fa81a5935b72eacbe29
        // decodeHex("acbe29") => 11320873
        Assert.assertEquals(11320873, strategy.getShardId(args));
    }
}
