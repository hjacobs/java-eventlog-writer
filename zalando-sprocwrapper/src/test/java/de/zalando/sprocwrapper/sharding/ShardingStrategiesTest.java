package de.zalando.sprocwrapper.sharding;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author  henning
 */
public class ShardingStrategiesTest {

    @Test
    public void testPerformance() {
        VirtualShardKeyStrategy strategy1 = new VirtualShardKeyFromStringUsingMd5();
        VirtualShardKeyStrategy strategy2 = new VirtualShardMurmur2HashStrategy();

        Object[] args = new Object[1];

        // sample SKU
        args[0] = "SE622H003-802000S000";

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            strategy1.getShardId(args);
        }

        long between = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            strategy2.getShardId(args);
        }

        long end = System.currentTimeMillis();
        System.out.println("MD5:     " + (between - start) + " ms");
        System.out.println("Murmur2: " + (end - between) + " ms");

    }

    @Test
    public void testVirtualShardMurmur2HashStrategy() {
        VirtualShardKeyStrategy strategy = new VirtualShardMurmur2HashStrategy();
        Assert.assertEquals(0, strategy.getShardId(null));
        Assert.assertEquals(0, strategy.getShardId(new Object[0]));
        Assert.assertEquals(0, strategy.getShardId(new Object[1]));

        Object[] args = new Object[1];
        args[0] = "A";

        Assert.assertTrue(strategy.getShardId(args) > 0);
    }

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
