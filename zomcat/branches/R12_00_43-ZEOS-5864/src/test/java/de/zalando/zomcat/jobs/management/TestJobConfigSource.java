package de.zalando.zomcat.jobs.management;

import de.zalando.zomcat.jobs.Job;
import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.JobConfigSource;

public class TestJobConfigSource implements JobConfigSource {

    @Override
    public String getAppInstanceKey() {
        return "local_local";
    }

    @Override
    public JobConfig getJobConfig(final Job job) {
        return null;
    }

}
