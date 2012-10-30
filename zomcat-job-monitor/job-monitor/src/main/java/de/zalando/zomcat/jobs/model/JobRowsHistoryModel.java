package de.zalando.zomcat.jobs.model;

import java.util.Map;

import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.common.collect.Maps;

import de.zalando.zomcat.jobs.JobsStatusBean;

public class JobRowsHistoryModel extends BaseLoadableDetachableModel<Map<Class<?>, Boolean>> {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private JobsStatusBean jobsStatusBean;

    private Map<Class<?>, Boolean> isHistoryEnabled;

    public JobRowsHistoryModel() { }

    @Override
    protected Map<Class<?>, Boolean> load() {

        if (isHistoryEnabled == null) {
            isHistoryEnabled = Maps.newHashMap();
        }

        return isHistoryEnabled;
    }

    public void toggleHistoryEnabled(final Class<?> clazz) {
        Boolean enabled = isHistoryEnabled.get(clazz);
        if (enabled == null) {
            enabled = true;
        } else {
            enabled = !enabled;
        }

        isHistoryEnabled.put(clazz, enabled);
    }

    public boolean isHistoryEnabled(final Class<?> clazz) {
        Boolean historyEnabled = isHistoryEnabled.get(clazz);
        if (historyEnabled == null) {
            historyEnabled = true;
            isHistoryEnabled.put(clazz, historyEnabled);
        }

        return historyEnabled;
    }

    public void put(final Class<?> beanClass, final boolean b) {
        isHistoryEnabled.put(beanClass, b);
    }
}
