package de.zalando.zomcat.jobs.batch;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.quartz.JobExecutionContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import de.zalando.utils.Pair;

import de.zalando.zomcat.jobs.AbstractJob;
import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.JobConfigSource;

public abstract class AbstractBulkProcessingJob<Item> extends AbstractJob {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractBulkProcessingJob.class);

    private static final String JOB_DATA_LIMIT_PARAMETER = "limit";

    private ItemFetcher<Item> fetcher;
    private ItemWriter<Item> writer;
    private ItemProcessor<Item> processor;

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private int processedCount;

    private int limit;

    protected JobConfigSource jobConfigSource;

    protected AbstractBulkProcessingJob() {
        super();
    }

    /**
     * Method fetches the items. If an error occures an exception is thrown.
     *
     * @param   limit
     *
     * @return
     */
    private List<Item> fetchItems(final int limit) {
        List<Item> items = Collections.emptyList();
        try {
            items = fetcher.fetchItems(limit);
        } catch (final Exception e) {
            final String message = "Could not fetch items";
            LOG.error(message, e);
            throw new IllegalStateException(message);
        }

        return items;
    }

    @Override
    public Integer getActualProcessedItemNumber() {
        return processedCount;
    }

    public ItemFetcher<Item> getFetcher() {
        return fetcher;
    }

    public ItemProcessor<Item> getProcessor() {
        return processor;
    }

    @Override
    public Integer getTotalNumberOfItemsToBeProcessed() {
        return limit;
    }

    public ItemWriter<Item> getWriter() {
        return writer;
    }

    private static void logFailedNumber(final boolean external, final int size) {
        if (LOG.isDebugEnabled() && (size > 0)) {
            String format = "validation failed count [%d]";
            if (external) {
                format = "external " + format;
            }

            LOG.debug(String.format(format, size));
        }
    }

    private void process(final int limit) {
        Preconditions.checkNotNull(fetcher, "fetcher must not be null");
        Preconditions.checkNotNull(processor, "processor must not be null");
        Preconditions.checkNotNull(writer, "writer must not be null");

        final List<JobResponse<Item>> successfulItems = Lists.newArrayList();
        final List<JobResponse<Item>> failedItems = Lists.newArrayList();

        LOG.info("starting {} batch job with limit {}", getBeanName(), limit);

        final List<Item> items = fetchItems(limit);

        if (items.isEmpty()) {

            // shortcut exit to avoid cluttering log files with unnecessary "0" items statements
            LOG.info("finished {} with 0 items", getBeanName());
            return;
        }

        LOG.info("processing {} items", items.size());

        // internal validation
        Pair<List<JobResponse<Item>>, List<JobResponse<Item>>> validatedItems = validate(items);

        List<JobResponse<Item>> validItems = validatedItems.getFirst();
        failedItems.addAll(validatedItems.getSecond());

        logFailedNumber(false, validatedItems.getSecond().size());

        // external validation
        try {
            List<String> errorMessages;
            for (JobResponse<Item> item : validItems) {
                try {
                    errorMessages = processor.validate(item.getItem());
                    if (errorMessages != null && !errorMessages.isEmpty()) {
                        item.addErrorMessages(errorMessages);
                        failedItems.add(item);
                    }
                } catch (Throwable e) {
                    item.addErrorMessage("Validation failed: " + e.getMessage());
                }
            }

            logFailedNumber(true, failedItems.size());

            if (!failedItems.isEmpty()) {
                LOG.info(String.format("Items to be processed with failed validation. Will mark them as ERROR. %s",
                        failedItems));
            }

        } catch (final Throwable t) {
            LOG.error("Critical error during validation of batch. Will mark ALL as ERROR.", t);
            validItems.clear();
            failedItems.clear();
            for (Item item : items) {
                failedItems.add(new JobResponse<Item>(item));
            }
        }

        for (final JobResponse<Item> item : validItems) {
            processedCount++;

            try {
                processor.process(item.getItem());
                successfulItems.add(item);
            } catch (final Throwable e) {
                LOG.error("Failed to process item [{}]", item.getItem(), e);

                final String message = e.getClass().getSimpleName() + " exception occured on processing item "
                        + item.getItem() + ": " + Throwables.getStackTraceAsString(e);

                item.addErrorMessage(message);
                failedItems.add(item);
            }
        }

        LOG.debug("writing {} successful and {} failed items", successfulItems.size(), failedItems.size());

        try {
            writer.writeItems(successfulItems, failedItems);
        } catch (Exception e) {
            LOG.error("Failed to write processing results with {} successfull and {} failed items",
                new Object[] {successfulItems.size(), failedItems.size(), e});
        }

        LOG.info("finished {} with {} successfull and {} failed items",
            new Object[] {getBeanName(), successfulItems.size(), failedItems.size()});
    }

    @Override
    public final void doRun(final JobExecutionContext executionContext, final JobConfig config) {
        setUp();
        try {
            limit = getLimit(config);
            if (limit == 0 && executionContext.getMergedJobDataMap().containsKey(JOB_DATA_LIMIT_PARAMETER)) {
                limit = executionContext.getMergedJobDataMap().getIntValue(JOB_DATA_LIMIT_PARAMETER);
            }

            process(limit);
        } catch (final Exception e) {
            LOG.error("Exception occured while processing with limit {}", limit, e);
        }
    }

    protected void setFetcher(final ItemFetcher<Item> fetcher) {
        this.fetcher = fetcher;
    }

    protected void setProcessor(final ItemProcessor<Item> processor) {
        this.processor = processor;
    }

    public abstract void setUp();

    protected void setWriter(final ItemWriter<Item> writer) {
        this.writer = writer;
    }

    /**
     * Validate the input items with the JSR-303 implementation. If violations are found the items is sorted to the
     * failed list, otherwise to the successful list.
     *
     * @param   items
     *
     * @return  a Pair of lists: first is the successful, second is the failed list
     */
    private Pair<List<JobResponse<Item>>, List<JobResponse<Item>>> validate(final List<Item> items) {
        final List<JobResponse<Item>> successfulItems = Lists.newArrayList();
        final List<JobResponse<Item>> failedItems = Lists.newArrayList();

        for (final Item item : items) {
            final Set<ConstraintViolation<Item>> violations = VALIDATOR.validate(item);
            if (violations.isEmpty()) {
                successfulItems.add(new JobResponse<Item>(item));
            } else {
                JobResponse<Item> response = new JobResponse<Item>(item);
                final List<String> messages = getMessageList(violations);
                response.addErrorMessages(messages);
                failedItems.add(response);
                LOG.warn("item {} failed validation {}", item, messages);
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
