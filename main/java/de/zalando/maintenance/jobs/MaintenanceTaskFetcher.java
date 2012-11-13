package de.zalando.maintenance.jobs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import de.zalando.maintenance.domain.MaintenanceTask;
import de.zalando.maintenance.domain.MaintenanceTaskStatus;
import de.zalando.maintenance.persistence.MaintenanceSProcService;

import de.zalando.zomcat.jobs.batch.transition.ItemFetcher;
import de.zalando.zomcat.jobs.batch.transition.ItemFetcherException;

@Component
public class MaintenanceTaskFetcher implements ItemFetcher<MaintenanceTask> {

    @Autowired
    private MaintenanceSProcService maintenanceSProcService;

    private List<MaintenanceTask> getAndUpdateDueMaintenanceTasks(final MaintenanceTaskStatus newStatus,
            final int limit) {
        return maintenanceSProcService.getAndUpdateDueMaintenanceTasks(newStatus, limit);
    }

    @Override
    public List<MaintenanceTask> fetchItems(final int limit) throws ItemFetcherException {
        final List<MaintenanceTask> beTasks = getAndUpdateDueMaintenanceTasks(MaintenanceTaskStatus.PROCESSING, limit);
        final List<MaintenanceTask> jobTasks = Lists.newArrayList();
        for (final MaintenanceTask beTask : beTasks) {

            jobTasks.add(beTask);
        }

        return jobTasks;
    }

    @Override
    public List<MaintenanceTask> enrichItems(final List<MaintenanceTask> items) throws Exception {
        return items;
    }
}
