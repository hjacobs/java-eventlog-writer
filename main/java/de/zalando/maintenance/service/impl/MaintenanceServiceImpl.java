package de.zalando.maintenance.service.impl;

import java.util.List;

import org.joda.time.DateTime;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import de.zalando.maintenance.domain.MaintenanceTask;
import de.zalando.maintenance.jobs.MaintenanceTaskExecutorProvider;
import de.zalando.maintenance.persistence.MaintenanceSProcService;
import de.zalando.maintenance.service.MaintenanceService;
import de.zalando.maintenance.service.MaintenanceTaskExecutor;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    @Autowired
    private MaintenanceSProcService maintenanceSProcService;

    @Autowired
    private MaintenanceTaskExecutorProvider maintenanceTaskExecutorProvider;

    @Override
    public List<MaintenanceTask> getMaintenanceTasks(final int limit) {
        return maintenanceSProcService.getAndUpdateDueMaintenanceTasks(null, limit);
    }

    @Override
    public Iterable<MaintenanceTaskExecutor> getMaintenanceTaskExecutors() {
        return maintenanceTaskExecutorProvider.getTaskExecutors();
    }

    @Override
    public void insertMaintenanceTask(final String parameter, final String taskType, final DateTime dueDate) {
        maintenanceSProcService.insertMaintenanceTask(parameter, taskType, dueDate);
    }

}
