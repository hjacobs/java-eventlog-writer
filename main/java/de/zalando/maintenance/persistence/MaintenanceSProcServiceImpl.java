package de.zalando.maintenance.persistence;

import java.util.List;

import org.joda.time.DateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Repository;

import com.google.common.base.Preconditions;

import de.zalando.maintenance.domain.MaintenanceTask;
import de.zalando.maintenance.domain.MaintenanceTaskStatus;
import de.zalando.maintenance.jobs.MaintenanceTaskExecutorProvider;

import de.zalando.sprocwrapper.AbstractSProcService;
import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;

@Repository
public class MaintenanceSProcServiceImpl extends AbstractSProcService<MaintenanceSProcService, DataSourceProvider>
    implements MaintenanceSProcService {

    @Autowired
    private MaintenanceTaskExecutorProvider maintenanceTaskExecutorProvider;

    @Autowired
    protected MaintenanceSProcServiceImpl(@Qualifier("maintenanceJobDataSourceProvider") final DataSourceProvider ps) {
        super(ps, MaintenanceSProcService.class);
    }

    @Override
    public List<MaintenanceTask> getAndUpdateDueMaintenanceTasks(final MaintenanceTaskStatus newStatus,
            final int limit) {
        return sproc.getAndUpdateDueMaintenanceTasks(newStatus, limit);
    }

    @Override
    public void insertMaintenanceTask(final String parameter, final String taskType, final DateTime dueDate) {
        Preconditions.checkArgument(maintenanceTaskExecutorProvider.isValid(taskType),
            "Incorrect value of taskType parameter.");
        sproc.insertMaintenanceTask(parameter, taskType, dueDate);
    }

    @Override
    public void updateMaintenanceTaskStatus(final List<Long> taskIds, final MaintenanceTaskStatus newStatus) {
        sproc.updateMaintenanceTaskStatus(taskIds, newStatus);
    }

}
