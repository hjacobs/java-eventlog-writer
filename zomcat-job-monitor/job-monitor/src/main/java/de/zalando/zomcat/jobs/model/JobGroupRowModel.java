package de.zalando.zomcat.jobs.model;

import java.util.List;
import java.util.Map;

import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.zalando.zomcat.jobs.JobGroupTypeStatusBean;
import de.zalando.zomcat.jobs.JobsStatusBean;

public class JobGroupRowModel extends BaseLoadableDetachableModel<List<JobGroupRow>> {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private JobsStatusBean jobsStatusBean;

    private Map<String, Boolean> visible;

    @Override
    protected List<JobGroupRow> load() {
        if (visible == null) {
            visible = Maps.newHashMap();
        }

        return Lists.newArrayList(Iterables.transform(jobsStatusBean.getJobGroupTypeStatusBeans(true),
                    new Function<JobGroupTypeStatusBean, JobGroupRow>() {
                        @Override
                        public JobGroupRow apply(final JobGroupTypeStatusBean input) {
                            if (!visible.containsKey(input.getJobGroupName())) {
                                visible.put(input.getJobGroupName(), true);
                            }

                            return new JobGroupRow(input.getJobGroupName(), visible.get(input.getJobGroupName()));
                        }
                    }));
    }

    public void toggleVisible(final String groupName) {
        Boolean isVisible = visible.get(groupName);
        if (isVisible == null) {
            isVisible = true;
        } else {
            isVisible = !isVisible;
        }

        visible.put(groupName, isVisible);
    }

    public boolean isVisible(final String jobGroupRowName) {
        Boolean isVisible = visible.get(jobGroupRowName);
        if (isVisible == null) {
            isVisible = true;
            visible.put(jobGroupRowName, isVisible);
        }

        return isVisible;
    }
}
