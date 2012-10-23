package de.zalando.zomcat.jobs.batch.transition;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Set;

import org.quartz.JobExecutionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.zalando.zomcat.jobs.AbstractJob;
import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.JobConfigSource;

public abstract class AbstractBulkProcessingJob<ITEM_TYPE> extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractBulkProcessingJob.class);

    private ItemFetcher<ITEM_TYPE> fetcher;
    private BatchExecutionStrategy<ITEM_TYPE> executionStrategy;
    private ItemProcessor<ITEM_TYPE> processor;
    private ItemWriter<ITEM_TYPE> writer;

    private WriteTime writeTime;

    private int limit;

    protected JobConfigSource jobConfigSource;
    protected JobExecutionContext jobExecutionContext;

    protected AbstractBulkProcessingJob() {
        super();
    }

    private void assertComponentsArePresent() {
        Set<String> missingComponents = Sets.newHashSet();
        if (fetcher == null) {
            missingComponents.add("fetcher");
        }

        if (executionStrategy == null) {
            missingComponents.add("executionStrategy");
        }

        if (processor == null) {
            missingComponents.add("processor");
        }

        if (writer == null) {
            missingComponents.add("writer");
        }

        checkState(missingComponents.isEmpty(), "Setup of %s incomplete. Missing fields are: %s.", getBeanName(),
            missingComponents);
    }

    /*
     * Configuration methods that must be implemented by the concrete implementations.
     */
    protected abstract ItemFetcher<ITEM_TYPE> getFetcher();

    protected abstract ItemProcessor<ITEM_TYPE> getProcessor();

    protected abstract ItemWriter<ITEM_TYPE> getWriter();

    protected abstract WriteTime getWriteTime();

    protected abstract BatchExecutionStrategy<ITEM_TYPE> getExecutionStrategy();

    /**
     * Method fetches and enriches the items. If an error occures an exception is thrown.
     *
     * @param   limit
     *
     * @return
     *
     * @throws  de.zalando.commons.backend.jobs.ItemFetcherException
     */
    private List<ITEM_TYPE> fetchItems(final int limit) throws Exception {
        try {
            return fetcher.fetchItems(limit);
        } catch (final ItemFetcherException e) {
            throw e;
        } catch (final Exception e) {
            final String message = "Could not fetch items";
            LOG.error(message, e);
            throw new ItemFetcherException(message, e);
        }
    }

    /**
     * Method enriches Items previously fetched.
     *
     * @param   items  Items to enrich
     *
     * @return  List of enriched Items
     *
     * @throws  ItemFetcherException  if an error occurs enriching Items
     */
    private List<ITEM_TYPE> enrichItems(final List<ITEM_TYPE> items) throws Exception {
        try {
            return fetcher.enrichItems(items);
        } catch (final ItemFetcherException e) {
            throw e;
        } catch (final Exception e) {
            final String message = "Could not enrich items";
            LOG.error(message, e);
            throw new ItemFetcherException(message, e);
        }
    }

    @Override
    public Integer getActualProcessedItemNumber() {
        return executionStrategy.getProcessedCount();
    }

    @Override
    public Integer getTotalNumberOfItemsToBeProcessed() {
        return limit;
    }

    private static void logFailedNumber(final boolean external, final int size) {
        if ((size > 0)) {
            LOG.debug("{}validation failed, count {}", external ? "external " : "", size);
        }
    }

    private void process(final int limit) throws Exception {
        checkArgument(limit >= 0, "limit must be >= 0");
        if (limit == 0) {

            // probably a configuration error, but we continue anyway
            LOG.warn("running {} with limit zero", getBeanName());
        }

        final List<ITEM_TYPE> successfulItems = Lists.newArrayList();
        final List<JobResponse<ITEM_TYPE>> failedItems = Lists.newArrayList();

        List<ITEM_TYPE> itemsToProcess = null;
        try {

            // NOTE: the bean name already ends with "Job" so we do not need to repeat it in log messages
            LOG.info("starting {} with limit {}", getBeanName(), limit);

            itemsToProcess = fetchItems(limit);

            if (itemsToProcess.isEmpty()) {

                // shortcut exit to avoid cluttering log files with unnecessary "0" items statements
                LOG.info("finished {} with 0 items", getBeanName());
                return;
            }

            LOG.info("processing {} items", itemsToProcess.size());

            itemsToProcess = enrichItems(itemsToProcess);

            LOG.info("enriched {} items", itemsToProcess.size());

            // From here on we should have no possibility of ItemFetcherException
            executionStrategy = getExecutionStrategy();
            executionStrategy.bind(processor, writer, writeTime);

            // executionStrategy.execute(validItems);
            executionStrategy.execute(itemsToProcess);

        } catch (final ItemFetcherException e) {

            // Clear Successful Item List for Writeback
            successfulItems.clear();

            // Check that actual Items were retrieved via Fetcher
            if (itemsToProcess != null) {

                // Assume that processing of ALL Items failed - could not reach processing stage when Enrich/Fetch
                // failed
                failedItems.addAll(Lists.transform(itemsToProcess,
                        new Function<ITEM_TYPE, JobResponse<ITEM_TYPE>>() {
                            @Override
                            public JobResponse<ITEM_TYPE> apply(final ITEM_TYPE item) {
                                JobResponse<ITEM_TYPE> jobResponse = new JobResponse<ITEM_TYPE>(item);
                                jobResponse.addErrorMessage(e.getMessage());
                                return jobResponse;
                            }
                            ;
                        }));
            }

            // if we write here we are probably not executing anything.
            LOG.debug(ItemWriter.WRITE_LOG_FORMAT, successfulItems.size(), failedItems.size());
            writer.writeItems(successfulItems, failedItems);

        }

        LOG.info("finished {} with {} successfull and {} failed items",
            new Object[] {getBeanName(), successfulItems.size(), failedItems.size()});
    }

    @Override
    public final void doRun(final JobExecutionContext executionContext, final JobConfig config) {

        this.fetcher = getFetcher();
        this.processor = getProcessor();
        this.writer = getWriter();
        this.writeTime = getWriteTime();
        this.executionStrategy = getExecutionStrategy();

        this.jobExecutionContext = executionContext;

        assertComponentsArePresent();

        try {
            limit = getLimit(config);
            process(limit);

        } catch (final Exception e) {
            LOG.error("Exception occured while processing with limit {}", limit, e);
        }
    }

}
