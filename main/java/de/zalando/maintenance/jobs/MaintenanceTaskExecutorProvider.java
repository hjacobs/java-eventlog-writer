package de.zalando.maintenance.jobs;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Repository;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import de.zalando.maintenance.service.MaintenanceTaskExecutor;

@Repository
public class MaintenanceTaskExecutorProvider {

    private Map<String, MaintenanceTaskExecutor> taskExecutorMap;

    public MaintenanceTaskExecutorProvider() {
        this.taskExecutorMap = Maps.newHashMap();
    }

    @Autowired(required = false)
    public MaintenanceTaskExecutorProvider(final List<MaintenanceTaskExecutor> taskExecutorList) {
        this.taskExecutorMap = Maps.uniqueIndex(taskExecutorList, new Function<MaintenanceTaskExecutor, String>() {
                    public String apply(final MaintenanceTaskExecutor input) {
                        return input.getTaskType();
                    }
                });
    }

    public MaintenanceTaskExecutor get(final String taskType) {
        if (!taskExecutorMap.containsKey(taskType)) {
            throw new IllegalArgumentException(String.format("Task type '%s' does not exists.", taskType));
        }

        return taskExecutorMap.get(taskType);
    }

    public Iterable<MaintenanceTaskExecutor> getTaskExecutors() {
        return taskExecutorMap.values();
    }

    public boolean isValid(final String taskType) {
        return taskExecutorMap.containsKey(taskType);
    }
}
