package de.zalando.zomcat.appconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import de.zalando.appconfig.Configuration;

import de.zalando.domain.Environment;

import de.zalando.zomcat.SystemConstants;

/**
 * @author  hjacobs
 */
public class BaseApplicationConfigImpl extends JobConfigSourceImpl implements BaseApplicationConfig {

    private static final Logger LOG = LoggerFactory.getLogger(BaseApplicationConfigImpl.class);
    protected static final String APPLICATION_ENVIRONMENT = "application.environment";

    protected transient Configuration config;

    @Override
    public Configuration getConfig() {
        return config;
    }

    public void setConfig(final Configuration config) {
        this.config = config;
    }

    @Override
    public String getAppInstanceKey() {

        final String key = SystemConstants.SYSTEM_PROPERTY_APP_INSTANCE_KEY;

        String appInstanceKey = System.getProperty(key);

        if (appInstanceKey == null) {

            final String defaultValue = SystemConstants.SYSTEM_NAME_FOR_LOCAL_INSTANCE;

            final String exclamation = Strings.repeat("!", 120);
            final StringBuilder builder = new StringBuilder(300);
            builder.append('\n').append(exclamation);
            builder.append("\nNo App Instance Key found, setting " + key + " = " + defaultValue);
            builder.append('\n').append(exclamation);
            LOG.error(builder.toString());

            System.setProperty(key, defaultValue);

            appInstanceKey = defaultValue;

        }

        return appInstanceKey;

    }

    /**
     * @return  never null
     *
     * @throws  IllegalArgumentException  the configured environment is invalid
     * @throws  IllegalStateException     no environment configured
     */
    @Override
    public Environment getEnvironment() {

        final String environment = config.getStringConfig(BaseApplicationConfigImpl.APPLICATION_ENVIRONMENT);
        if (environment == null) {
            throw new IllegalStateException();
        }

        return Environment.valueOf(environment);

    }

    /**
     * @return  true if configured as test machine, false otherwise
     *
     * @throws  IllegalArgumentException  the configured environment is invalid
     * @throws  IllegalStateException     no environment configured
     */
    @Override
    public boolean isTesting() {
        return !getEnvironment().isLive();
    }

    /**
     * @return  true if configured as local machine, false otherwise
     *
     * @throws  IllegalArgumentException  the configured environment is invalid
     * @throws  IllegalStateException     no environment configured
     */
    @Override
    public boolean isLocalMachine() {
        return Environment.LOCAL == getEnvironment();
    }

}
