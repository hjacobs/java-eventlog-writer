package de.zalando.zomcat.jobs.batch.example.strategy;

import java.util.Collection;

import de.zalando.zomcat.jobs.batch.transition.ItemFinalizer;
import de.zalando.zomcat.jobs.batch.transition.JobResponse;

public class FakeFinalizer implements ItemFinalizer<FakeItem> {

    private Collection<FakeItem> successfulItems;
    private Collection<JobResponse<FakeItem>> failedItems;

    @Override
    public void finalizeItems(final Collection<FakeItem> successfulItems,
            final Collection<JobResponse<FakeItem>> failedItems) {
        this.successfulItems = successfulItems;
        this.failedItems = failedItems;
    }

    public Collection<FakeItem> getSuccessfulItems() {
        return successfulItems;
    }

    public Collection<JobResponse<FakeItem>> getFailedItems() {
        return failedItems;
    }
}
