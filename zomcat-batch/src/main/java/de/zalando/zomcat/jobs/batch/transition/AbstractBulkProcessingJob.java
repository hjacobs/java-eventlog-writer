package de.zalando.zomcat.jobs.batch.transition;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.quartz.JobExecutionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.zalando.utils.Pair;

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

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private int limit;

    protected JobConfigSource jobConfigSource;

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

        executionStrategy = getExecutionStrategy();
        executionStrategy.bind(processor, writer, writeTime, successfulItems, failedItems);

        List<ITEM_TYPE> itemsToProcess = null;
        try {

            // NOTE: the bean name already ends with "Job" so we do not need to repeat it in log messages
            LOG.info("starting {} with limit {}", getBeanName(), limit);

            itemsToProcess = fetchItems(limit);

            LOG.info("processing {} items", itemsToProcess.size());

            itemsToProcess = enrichItems(itemsToProcess);

            LOG.info("enriched {} items", itemsToProcess.size());

            // internal validation
            Pair<Collection<ITEM_TYPE>, Collection<JobResponse<ITEM_TYPE>>> validatedItems = validate(itemsToProcess);

            Collection<ITEM_TYPE> validItems = validatedItems.getFirst();
            failedItems.addAll(validatedItems.getSecond());

            logFailedNumber(false, validatedItems.getSecond().size());

            // external validation
            try {
                validatedItems = processor.validate(validItems);

                validItems = validatedItems.getFirst();
                failedItems.addAll(validatedItems.getSecond());
                logFailedNumber(true, validatedItems.getSecond().size());

                if (!failedItems.isEmpty()) {
                    LOG.info("Items to be processed with failed validation. Will mark them as ERROR. {}", failedItems);
                }

            } catch (final Throwable t) {
                LOG.error("Critical error during validation of batch. Will mark ALL as ERROR.", t);

                validItems.clear();
                failedItems.clear();
                failedItems.addAll(Lists.transform(itemsToProcess,
                        new Function<ITEM_TYPE, JobResponse<ITEM_TYPE>>() {
                            @Override
                            public JobResponse<ITEM_TYPE> apply(final ITEM_TYPE item) {
                                return new JobResponse<ITEM_TYPE>(item);
                            }
                            ;
                        }));
            }

            // from here on we should have no possibility of ItemFetcherException

            executionStrategy.execute(validItems);

        } catch (final ItemFetcherException e) {

            // Clear Successful Item List for Writeback
            successfulItems.clear();

            // Check that actual Items were retrieved via Fetcher
            if (itemsToProcess != null) {

                // Add current Error to all retrieved Items
// for (final ITEM_TYPE curItem : itemsToProcess) {
// curItem.addErrorMessage(e.getMessage());
// }

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

        LOG.info("finished {} with {} successfull items and {} failed items",
            new Object[] {getBeanName(), successfulItems.size(), failedItems.size()});
    }

    /**
     * @param  successfulItems
     * @param  failedItems
     * @param  item
     */
    /*
     * public void processSingleItem(final List<JobResponse<ITEM_TYPE>> successfulItems,
     *      final List<JobResponse<ITEM_TYPE>> failedItems, final JobResponse<ITEM_TYPE> item) {
     *  try {
     *
     *      processor.process(item);
     *      successfulItems.add(item);
     *
     *  } catch (final Throwable e) {
     *      LOG.error("Failed to process item [{}]", item, e);
     *
     *      final String message = e.getClass().getSimpleName() + " exception occured on processing item " + item + ": "
     *              + Throwables.getStackTraceAsString(e);
     *
     *      item.addErrorMessage(message);
     *      failedItems.add(item);
     *  }
     *}*/

    @Override
    public final void doRun(final JobExecutionContext executionContext, final JobConfig config) {

        fetcher = getFetcher();
        processor = getProcessor();
        writer = getWriter();
        writeTime = getWriteTime();
        executionStrategy = getExecutionStrategy();

        assertComponentsArePresent();

        try {
            limit = getLimit(config);
            process(limit);

        } catch (final Exception e) {
            LOG.error("Exception occured while processing with limit {}", limit, e);
        }
    }

    /**
     * Validate the input items with the JSR-303 implementation. If violations are found the items is sorted to the
     * failed list, otherwise to the successful list.
     *
     * @param   items
     *
     * @return  a Pair of lists: first is the successful, second is the failed list
     */
    private Pair<Collection<ITEM_TYPE>, Collection<JobResponse<ITEM_TYPE>>> validate(final List<ITEM_TYPE> items) {
        final Collection<ITEM_TYPE> successfulItems = Lists.newArrayList();
        final Collection<JobResponse<ITEM_TYPE>> failedItems = Lists.newArrayList();

        for (final ITEM_TYPE item : items) {
            final Set<ConstraintViolation<ITEM_TYPE>> violations = VALIDATOR.validate(item);
            if (violations.isEmpty()) {
                successfulItems.add(item);
            } else {
                final List<String> messages = getMessageList(violations);
                JobResponse<ITEM_TYPE> response = new JobResponse<ITEM_TYPE>(item);
                response.addErrorMessages(messages);
                failedItems.add(response);
                LOG.warn("item [{}] failed validation [{}]", item, messages);
            }
        }

        return Pair.of(successfulItems, failedItems);
    }

    public static <T> List<String> getMessageList(final Set<ConstraintViolation<T>> violations) {
        List<String> messages = Lists.newArrayListWithCapacity(violations.size());
        for (final ConstraintViolation<?> violation : violations) {
            messages.add(String.format("invalid value [%s] of property [%s] %s", violation.getInvalidValue(),
                    violation.getPropertyPath(), violation.getMessage()));
        }

        return messages;
    }

}
