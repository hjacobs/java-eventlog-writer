package de.zalando.zomcat.jobs;

import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

@Component
public class JobConfigSourceImpl implements JobConfigSource {

    @Override
    public String getAppInstanceKey() {
        return "host1";
    }

    @Override
    public JobConfig getJobConfig(final Job job) {
        if (job instanceof TestJob1) {
            final JobGroupConfig jobGroupConfig = new JobGroupConfig(job.getJobGroup().groupName(), true,
                    Sets.newHashSet("host1", "host2"));
            return new JobConfig(Sets.newHashSet("host1"), 10, 5, true, jobGroupConfig);
        }

        if (job instanceof TestJob2) {
            final JobGroupConfig jobGroupConfig = new JobGroupConfig(job.getJobGroup().groupName(), true,
                    Sets.newHashSet("host2"));
            return new JobConfig(Sets.newHashSet("host1"), 10, 5, true, jobGroupConfig);
        }

        if (job instanceof TestJob3) {
            final JobGroupConfig jobGroupConfig = new JobGroupConfig(job.getJobGroup().groupName(), true,
                    Sets.newHashSet("host1"));
            return new JobConfig(Sets.newHashSet("host1"), 10, 5, true, jobGroupConfig);
        }

        return null;
    }

}
