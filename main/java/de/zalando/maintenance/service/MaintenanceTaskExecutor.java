package de.zalando.maintenance.service;

import java.util.List;

public interface MaintenanceTaskExecutor {

    /**
     * @param   parameter
     *
     * @return  a list of error messages, empty list if it was successful
     */
    List<String> execute(String parameter);

    String getTaskType();

    String getTaskDescription();

}
