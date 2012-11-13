package de.zalando.maintenance.jobs;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

import de.zalando.maintenance.domain.MaintenanceTask;
import de.zalando.maintenance.domain.MaintenanceTaskStatus;
import de.zalando.maintenance.persistence.MaintenanceSProcService;

import de.zalando.zomcat.jobs.batch.transition.ItemWriter;
import de.zalando.zomcat.jobs.batch.transition.ItemWriterException;
import de.zalando.zomcat.jobs.batch.transition.JobResponse;

@Component
public class MaintenanceTaskWriter implements ItemWriter<MaintenanceTask> {

    @Autowired
    private MaintenanceSProcService maintenanceSProcService;

    @Override
    public void writeItems(final Collection<MaintenanceTask> successfulItems,
            final Collection<JobResponse<MaintenanceTask>> failedItems) throws ItemWriterException {

        // collect id of tasks that were successful
        final List<Long> successfulIds = Lists.newArrayList();
        for (final MaintenanceTask task : successfulItems) {
            successfulIds.add(task.getId());
        }

        // collect id of tasks that failed
        final List<Long> failedIds = Lists.newArrayList();
        for (final JobResponse<MaintenanceTask> jobResponse : failedItems) {
            failedIds.add(jobResponse.getJobItem().getId());
        }

        // set status of successful tasks to PROCESSED
        if (successfulIds.size() > 0) {
            maintenanceSProcService.updateMaintenanceTaskStatus(successfulIds, MaintenanceTaskStatus.PROCESSED);
        }

        // set status of successful tasks to FAILED
        if (failedIds.size() > 0) {
            maintenanceSProcService.updateMaintenanceTaskStatus(failedIds, MaintenanceTaskStatus.FAILED);
        }
    }
}
