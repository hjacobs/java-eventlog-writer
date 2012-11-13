package de.zalando.maintenance.service;

import java.util.List;

import org.joda.time.DateTime;

import de.zalando.maintenance.domain.MaintenanceTask;

public interface MaintenanceService {

    List<MaintenanceTask> getMaintenanceTasks(int limit);

    Iterable<MaintenanceTaskExecutor> getMaintenanceTaskExecutors();

    void insertMaintenanceTask(String parameter, String taskType, DateTime dueDate);

}
