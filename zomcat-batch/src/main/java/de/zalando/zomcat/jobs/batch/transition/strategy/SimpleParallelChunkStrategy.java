package de.zalando.zomcat.jobs.batch.transition.strategy;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class SimpleParallelChunkStrategy<ITEM_TYPE> extends ParallelChunkBulkProcessingExecutionStrategy<ITEM_TYPE> {

    private int numberOfChunks;

    public SimpleParallelChunkStrategy(final int numberOfChunks) {
        super();
        setNumberOfChunks(numberOfChunks);
    }

    @Override
    public Map<String, Collection<ITEM_TYPE>> makeChunks(final Collection<ITEM_TYPE> items) {

        int i = 0;
        Map<String, Collection<ITEM_TYPE>> result = Maps.newHashMap();
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

    protected final void setNumberOfChunks(final int numberOfChunks) {
        Preconditions.checkArgument(numberOfChunks > 0, "number of chunks must be a positive number");
        this.numberOfChunks = numberOfChunks;
    }

}
