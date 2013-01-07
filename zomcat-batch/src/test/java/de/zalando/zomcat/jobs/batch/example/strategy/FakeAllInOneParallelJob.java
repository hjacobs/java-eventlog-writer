package de.zalando.zomcat.jobs.batch.example.strategy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.zalando.zomcat.jobs.batch.transition.AbstractBulkProcessingJob;
import de.zalando.zomcat.jobs.batch.transition.BatchExecutionStrategy;
import de.zalando.zomcat.jobs.batch.transition.ItemFetcher;
import de.zalando.zomcat.jobs.batch.transition.ItemProcessor;
import de.zalando.zomcat.jobs.batch.transition.ItemWriter;
import de.zalando.zomcat.jobs.batch.transition.JobResponse;
import de.zalando.zomcat.jobs.batch.transition.WriteTime;
import de.zalando.zomcat.jobs.batch.transition.contextaware.ExecutionContextAwareJobStep;
import de.zalando.zomcat.jobs.batch.transition.strategy.ParallelChunkBulkProcessingExecutionStrategy;

/**
 * Sample implementation of a linear job. Reads a simple file and processes its content.
 *
 * @author  john
 */
public class FakeAllInOneParallelJob extends AbstractBulkProcessingJob<FakeItem> implements ItemFetcher<FakeItem>,
    ItemProcessor<FakeItem>, ItemWriter<FakeItem>, FakeJob, ExecutionContextAwareJobStep {

    private static final Logger LOG = LoggerFactory.getLogger(FakeAllInOneParallelJob.class);

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
        return new ParallelChunkBulkProcessingExecutionStrategy<FakeItem>() {

            @Override
            public Map<String, Collection<FakeItem>> makeChunks(final Collection<FakeItem> items) {
                final Map<String, Collection<FakeItem>> m = Maps.newHashMap();
                int i = 0;
                Iterable<List<FakeItem>> partition = Iterables.partition(items, chunkSize);
                for (List<FakeItem> list : partition) {
                    m.put(Integer.toString(i++), list);
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

        Preconditions.checkNotNull("For testing source file must be set!", sourceFileName);

        Preconditions.checkNotNull(getJobDataMap());

        final FileReader fileReader = new FileReader(sourceFileName);
        final BufferedReader br = new BufferedReader(fileReader);
        String line = null;

        final List<FakeItem> r = Lists.newArrayList();
        while ((line = br.readLine()) != null) {
            FakeItem fakeItemFromLine = readFakeItemFromLine(line);
            r.add(fakeItemFromLine);

        }

        getLocalJobExecutionContext().put("FAKE_DATA", System.currentTimeMillis());

        return r;
    }

    private FakeItem readFakeItemFromLine(final String line) {
        final String[] split = line.split(" ");

        final FakeItem f = new FakeItem();
        f.setId(Integer.parseInt(split[0]));
        f.setFailed(Integer.parseInt(split[1]) == 0);
        f.setText(split[2]);
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

        Preconditions.checkNotNull(getJobDataMap());
        Preconditions.checkNotNull(getLocalJobExecutionContext().get("FAKE_DATA"));

        LOG.trace("using output file: " + logFile);
        synchronized (logFile) {

            FileWriter fileWriter = null;
            try {

                fileWriter = new FileWriter(logFile, true);
                fileWriter.append(String.format("%s %s\n", successfulItems.size(), failedItems.size()));
                fileWriter.close();
            } catch (final IOException ex) {
                throw new IllegalStateException("Failed to write file", ex);
            }
        }
    }

    @Override
    public void process(final FakeItem item) throws Exception {
        if (item.isFailed()) {
            throw new IllegalArgumentException("Simulating failure.");
        }

        Preconditions.checkNotNull(getJobDataMap());
        Preconditions.checkNotNull(getLocalJobExecutionContext().get("FAKE_DATA"));

        item.setProcessed(true);

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
    private final int count = 0;

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

    @Override
    public void setExecutionContext(final JobExecutionContext dummyExecutionContext) {
        this.executionContext = dummyExecutionContext;
    }

    /**
     * For Execution context awareness.
     */
    private JobExecutionContext jobExecutionContext;
    private Map<String, Object> localJobExecutionContext;

    @Override
    public void setJobExecutionContext(final JobExecutionContext jobExecutionContext) {
        this.jobExecutionContext = jobExecutionContext;

    }

    @Override
    public void setLocalJobExecutionContext(final Map<String, Object> localJobExecutionContext) {
        this.localJobExecutionContext = localJobExecutionContext;

    }

    public JobExecutionContext getJobExecutionContext() {
        return jobExecutionContext;
    }

    public Map<String, Object> getLocalJobExecutionContext() {
        return localJobExecutionContext;
    }
}
