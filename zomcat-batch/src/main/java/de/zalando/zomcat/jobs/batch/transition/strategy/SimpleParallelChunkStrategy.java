package de.zalando.zomcat.jobs.batch.transition.strategy;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class SimpleParallelChunkStrategy<ITEM_TYPE> extends ParallelChunkBulkProcessingExecutionStrategy<ITEM_TYPE> {

    private int numberOfChunks;

    public SimpleParallelChunkStrategy(final int numberOfChunks) {
        super();
        this.numberOfChunks = numberOfChunks;
    }

    @Override
    public Map<String, Collection<ITEM_TYPE>> makeChunks(final Collection<ITEM_TYPE> items) {
        int i = 0;
        Map<String, Collection<ITEM_TYPE>> result = new HashMap<String, Collection<ITEM_TYPE>>();
        for (ITEM_TYPE item : items) {
            String chunk = new Integer(i % numberOfChunks).toString();
            if (!result.containsKey(chunk)) {
                result.put(chunk, new LinkedList<ITEM_TYPE>());
            }

            result.get(chunk).add(item);
            i++;
        }

        return result;
    }

    protected int getNumberOfChunks() {
        return numberOfChunks;
    }

    protected void setNumberOfChunks(final int numberOfChunks) {
        this.numberOfChunks = numberOfChunks;
    }

}
