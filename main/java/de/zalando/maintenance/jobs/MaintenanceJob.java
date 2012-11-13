package de.zalando.maintenance.jobs;

import de.zalando.maintenance.domain.MaintenanceTask;

import de.zalando.zomcat.jobs.batch.transition.AbstractLinearBulkProcessingJob;
import de.zalando.zomcat.jobs.batch.transition.ItemFetcher;
import de.zalando.zomcat.jobs.batch.transition.ItemProcessor;
import de.zalando.zomcat.jobs.batch.transition.ItemWriter;
import de.zalando.zomcat.jobs.batch.transition.WriteTime;

public class MaintenanceJob extends AbstractLinearBulkProcessingJob<MaintenanceTask> {

    private static final String BEAN_NAME = "maintenanceJob";
    private static final String JOB_DESCRIPTION =
        "Fetches due maintenance tasks and hands them over to the appropriate executor";

    @Override
    public String getBeanName() {
        return BEAN_NAME;
    }

    @Override
    public String getDescription() {
        return JOB_DESCRIPTION;
    }

    @Override
    protected ItemFetcher<MaintenanceTask> getFetcher() {
        return getApplicationContext().getBean(MaintenanceTaskFetcher.class);
    }

    @Override
    protected ItemProcessor<MaintenanceTask> getProcessor() {
        return getApplicationContext().getBean(MaintenanceTaskProcessor.class);
    }

    @Override
    protected ItemWriter<MaintenanceTask> getWriter() {
        return getApplicationContext().getBean(MaintenanceTaskWriter.class);
    }

    @Override
    protected WriteTime getWriteTime() {
        return WriteTime.AT_END_OF_BATCH;
    }

    @Override
    protected String getLockResource() {
        return getBeanName();
    }
}
