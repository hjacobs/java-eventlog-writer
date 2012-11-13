package de.zalando.maintenance.persistence;

import java.util.List;

import org.joda.time.DateTime;

import de.zalando.maintenance.domain.MaintenanceTask;
import de.zalando.maintenance.domain.MaintenanceTaskStatus;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;

public interface MaintenanceSProcService {

    @SProcCall(readOnly = true)
    List<MaintenanceTask> getAndUpdateDueMaintenanceTasks(@SProcParam final MaintenanceTaskStatus newStatus,
            @SProcParam final int limit);

    @SProcCall
    void insertMaintenanceTask(@SProcParam final String parameter, @SProcParam final String type,
            @SProcParam final DateTime dueDate);

    @SProcCall
    void updateMaintenanceTaskStatus(@SProcParam final List<Long> taskIds,
            @SProcParam final MaintenanceTaskStatus newStatus);
}
