package de.zalando.zomcat.jobs.batch.example.strategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.zalando.utils.Pair;

import de.zalando.zomcat.jobs.batch.transition.AbstractBulkProcessingJob;
import de.zalando.zomcat.jobs.batch.transition.BatchExecutionStrategy;
import de.zalando.zomcat.jobs.batch.transition.ItemFetcher;
import de.zalando.zomcat.jobs.batch.transition.ItemProcessor;
import de.zalando.zomcat.jobs.batch.transition.ItemWriter;
import de.zalando.zomcat.jobs.batch.transition.JobResponse;
import de.zalando.zomcat.jobs.batch.transition.WriteTime;
import de.zalando.zomcat.jobs.batch.transition.strategy.SingleThreadedChunkedBatchExecutionStrategy;

/**
 * Sample implementation of a linear job. Reads a simple file and processes its content. Processing rule is that if
 *
 * @author  john
 */
public class FakeChunkedJob extends AbstractBulkProcessingJob<FakeItem> implements ItemFetcher<FakeItem>,
    ItemProcessor<FakeItem>, ItemWriter<FakeItem>, FakeJob {

    private static final Logger LOG = LoggerFactory.getLogger(FakeChunkedJob.class);

    @Override
    public String getDescription() {

        return "Simple Fake Linear Job";
    }

    @Override
    protected ItemFetcher<FakeItem> getFetcher() {

        return this;
    }

    @Override
    protected ItemProcessor<FakeItem> getProcessor() {

        return this;
    }

    @Override
    protected ItemWriter<FakeItem> getWriter() {

        return this;
    }

    @Override
    protected BatchExecutionStrategy<FakeItem> getExecutionStrategy() {
        return new SingleThreadedChunkedBatchExecutionStrategy<FakeItem>() {

            @Override
            protected Pair<List<FakeItem>, List<JobResponse<FakeItem>>> joinResults() {
                return Pair.of(successfulItems, failedItems);
            }

            @Override
            public Map<String, Collection<FakeItem>> makeChunks(final Collection<FakeItem> items) {
                Map<String, Collection<FakeItem>> m = Maps.newHashMap();
                int chunkId = 0;

                int c = 0;

                Collection<FakeItem> l = Lists.newArrayList();

                for (FakeItem fakeItem : items) {

                    c++;
                    l.add(fakeItem);

                    if (c >= chunkSize) {

                        m.put(new Integer(chunkId).toString(), l);

                        c = 0;
                        chunkId++;
                        l = Lists.newArrayList();
                    }
                }

                return m;
            }

        };
    }

    @Override
    protected WriteTime getWriteTime() {

        if (writeTime == null) {
            throw new IllegalStateException("For testing write time must be set!");
        }

        return writeTime;
    }

    @Override
    public List<FakeItem> fetchItems(final int limit) throws Exception {

        if (sourceFileName == null) {
            throw new IllegalStateException("For testing source file must be set!");
        }

        FileReader fileReader = new FileReader(sourceFileName);
        BufferedReader br = new BufferedReader(fileReader);
        String line = null;

        List<FakeItem> r = Lists.newArrayList();
        while ((line = br.readLine()) != null) {
            r.add(readFakeItemFromLine(line));
        }

        return r;
    }

    private FakeItem readFakeItemFromLine(final String line) {
        String[] split = line.split(" ");

        FakeItem f = new FakeItem();
        f.setId(Integer.parseInt(split[0]));
        f.setText(split[1]);
        return f;
    }

    @Override
    public List<FakeItem> enrichItems(final List<FakeItem> items) throws Exception {

        return items;
    }

    @Override
    public void writeItems(final Collection<FakeItem> successfulItems,
            final Collection<JobResponse<FakeItem>> failedItems) {

        if (logFile == null) {
            throw new IllegalStateException("For testing logFile must be set.");
        }

        LOG.debug("using output file: " + logFile);

        FileWriter fileWriter = null;
        try {

            fileWriter = new FileWriter(logFile, true);
            fileWriter.append(String.format("%s %s\n", successfulItems.size(), failedItems.size()));
            fileWriter.close();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write file", ex);
        }

    }

    @Override
    public void process(final FakeItem item) throws Exception {

        count++;

        if (count % 10 == 0) {
            throw new IllegalArgumentException("Simulating failure.");
        }

    }

    @Override
    public void validate(final FakeItem item) { }

    /*
     * These fields are used only to test since this is a fake job. "Real" jobs won't require any of this.
     *
     */

    private WriteTime writeTime;
    private String sourceFileName;
    private File logFile;
    private int chunkSize;
    private int count = 0;

    @Override
    public void setWriteTime(final WriteTime writeTime) {
        this.writeTime = writeTime;
    }

    @Override
    public void setSourceFileName(final String sourceFile) {
        this.sourceFileName = sourceFile;
    }

    @Override
    public void setLogFile(final File logFile) {
        this.logFile = logFile;
    }

    @Override
    public void setChunkSize(final int chunkSize) {
        this.chunkSize = chunkSize;
    }

}
