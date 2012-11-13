package de.zalando.maintenance.jmx;

import org.joda.time.DateTime;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import org.springframework.stereotype.Component;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;

import de.zalando.maintenance.service.MaintenanceService;
import de.zalando.maintenance.service.MaintenanceTaskExecutor;

@ManagedResource(objectName = "Zalando:type=Maintenance,name=Maintenance Job Bean")
@Component
public class MaintenanceJobMBean {

    private static final Joiner JOINER = Joiner.on("," + System.getProperty("line.separator"));

    private static final Function<MaintenanceTaskExecutor, String> EXECUTOR_TO_STRING =
        new Function<MaintenanceTaskExecutor, String>() {
            public String apply(final MaintenanceTaskExecutor input) {
                return String.format("%s: %s", input.getTaskType(), input.getTaskDescription());
            }
        };

    @Autowired
    private MaintenanceService maintenanceService;

    @ManagedOperation
    @ManagedOperationParameters(
        { @ManagedOperationParameter(name = "limit", description = "limit the number of tasks to query for") }
    )
    public String getDueMaintenanceTasks(final int limit) {
        return JOINER.join(maintenanceService.getMaintenanceTasks(limit));
    }

    @ManagedOperation
    public String getMaintenanceTaskTypeExecutors() {
        return JOINER.join(Iterables.transform(maintenanceService.getMaintenanceTaskExecutors(), EXECUTOR_TO_STRING));
    }

    @ManagedOperation
    @ManagedOperationParameters(
        {
            @ManagedOperationParameter(name = "parameter", description = "executor method parameter"),
            @ManagedOperationParameter(
                name = "taskType", description = "one of getMaintenanceTaskTypeExecutors()"
            ),
            @ManagedOperationParameter(
                name = "dueDate",
                description = "date after which this task should be executed (eg: 2012-11-14T11:14:00)"
            )
        }
    )
    public void insertMaintenanceTask(final String parameter, final String taskType, final String dueDate) {
        maintenanceService.insertMaintenanceTask(parameter, taskType, DateTime.parse(dueDate));
    }

}
