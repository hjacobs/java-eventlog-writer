package de.zalando.maintenance.jobs;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import com.google.common.base.Joiner;

import de.zalando.maintenance.domain.MaintenanceTask;
import de.zalando.maintenance.service.MaintenanceTaskExecutor;

import de.zalando.zomcat.jobs.batch.transition.ItemProcessor;
import de.zalando.zomcat.jobs.batch.transition.ItemProcessorException;

@Component
public class MaintenanceTaskProcessor implements ItemProcessor<MaintenanceTask> {

    @Autowired
    private MaintenanceTaskExecutorProvider executorProvider;

    @Override
    public void process(final MaintenanceTask task) {
        final String beanName = task.getType();
        MaintenanceTaskExecutor executor = executorProvider.get(beanName);
        final List<String> s = executor.execute(task.getParameter());
        if (CollectionUtils.isNotEmpty(s)) {
            throw new ItemProcessorException(Joiner.on("; ").join(s));
        }
    }

    @Override
    public void validate(final MaintenanceTask item) {
        // TODO Auto-generated method stub

    }
}
