package de.zalando.zomcat.appconfig;

import de.zalando.appconfig.Configuration;

import de.zalando.domain.Environment;

import de.zalando.zomcat.jobs.JobConfigSource;

/**
 * @author  hjacobs
 */
public interface BaseApplicationConfig extends JobConfigSource {

    String BEAN_NAME = "applicationConfig";

    /**
     * @return  flag if this is a test system
     */
    boolean isTesting();

    /**
     * @return  the {@link Environment Environment}
     */
    Environment getEnvironment();

    boolean isLocalMachine();

    Configuration getConfig();

}
