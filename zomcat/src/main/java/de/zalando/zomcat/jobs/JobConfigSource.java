package de.zalando.zomcat.jobs;

import de.zalando.domain.ComponentBean;

import de.zalando.zomcat.configuration.AppInstanceKeySource;

public interface JobConfigSource extends AppInstanceKeySource {

    /**
     * Fetch JobConfig for given {@link ComponentBean} instance.
     *
     * @param   job  The Job Spring Bean (ComponentBean) to fetch JobConfig for
     *
     * @return  The JobConfig created for given Job
     */
    JobConfig getJobConfig(ComponentBean job);

}
