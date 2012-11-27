package de.zalando.zomcat.jobs.batch.example.strategy;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;

import de.zalando.zomcat.jobs.batch.transition.strategy.SimpleParallelChunkStrategy;

public class SimpleParallelChunkStrategyTest {

    @Test
    public void chunkStrings() {
        SimpleParallelChunkStrategy<String> chunker = new SimpleParallelChunkStrategy<String>(3);
        Map<String, Collection<String>> result = chunker.makeChunks(Arrays.asList("1", "2", "3", "1", "2", "3"));
        assertEquals(3, result.size());
        for (String key : result.keySet()) {
            assertEquals(2, result.get(key).size());
        }
    }

}
