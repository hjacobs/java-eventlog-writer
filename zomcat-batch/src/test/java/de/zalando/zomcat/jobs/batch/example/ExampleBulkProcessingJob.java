package de.zalando.zomcat.jobs.batch.example;

import java.util.List;

import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.batch.AbstractBulkProcessingJob;
import de.zalando.zomcat.jobs.batch.ItemFetcher;
import de.zalando.zomcat.jobs.batch.ItemProcessor;
import de.zalando.zomcat.jobs.batch.ItemWriter;
import de.zalando.zomcat.jobs.batch.JobResponse;

/**
 * @author  hjacobs
 */
public class ExampleBulkProcessingJob extends AbstractBulkProcessingJob<ExampleItem>
    implements ItemFetcher<ExampleItem>, ItemProcessor<ExampleItem>, ItemWriter<ExampleItem> {

    private List<ExampleItem> items;
    private List<JobResponse<ExampleItem>> successfulItems;
    private List<JobResponse<ExampleItem>> failedItems;

    public List<ExampleItem> getItems() {
        return items;
    }

    public void setItems(final List<ExampleItem> items) {
        this.items = items;
    }

    public List<JobResponse<ExampleItem>> getFailedItems() {
        return failedItems;
    }

    public void setFailedItems(final List<JobResponse<ExampleItem>> failedItems) {
        this.failedItems = failedItems;
    }

    public List<JobResponse<ExampleItem>> getSuccessfulItems() {
        return successfulItems;
    }

    public void setSuccessfulItems(final List<JobResponse<ExampleItem>> successfulItems) {
        this.successfulItems = successfulItems;
    }

    @Override
    public void setUp() {
        setFetcher(this);
        setProcessor(this);
        setWriter(this);
    }

    @Override
    public String getDescription() {
        return "Example Batch Job";
    }

    @Override
    public List<ExampleItem> fetchItems(final int limit) throws Exception {
        return items;
    }

    @Override
    public void process(final ExampleItem item) throws Exception {
        item.setText("TEST");
    }

    @Override
    public List<String> validate(final ExampleItem item) {
        return null;
    }

    @Override
    public void writeItems(final List<JobResponse<ExampleItem>> successfulItems,
            final List<JobResponse<ExampleItem>> failedItems) throws Exception {
        this.successfulItems = successfulItems;
        this.failedItems = failedItems;
    }

    @Override
    protected int getLimit(final JobConfig config) {
        return 10;
    }

}
