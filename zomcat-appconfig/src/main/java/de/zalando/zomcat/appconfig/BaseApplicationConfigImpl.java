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
        String appInstanceKey = System.getProperty(SystemConstants.SYSTEM_PROPERTY_APP_INSTANCE_KEY);
        if (appInstanceKey == null) {
            final String exclamation = Strings.repeat("!", 120);
            final StringBuilder builder = new StringBuilder(300);
            builder.append('\n').append(exclamation);
            builder.append("\nNo App Instance Key found, setting " + SystemConstants.SYSTEM_PROPERTY_APP_INSTANCE_KEY
                    + " = " + SystemConstants.SYSTEM_NAME_FOR_LOCAL_INSTANCE);
            builder.append('\n').append(exclamation);
            LOG.error(builder.toString());

            System.setProperty(SystemConstants.SYSTEM_PROPERTY_APP_INSTANCE_KEY,
                SystemConstants.SYSTEM_NAME_FOR_LOCAL_INSTANCE);

            // set the appInstanceKey to default:
            appInstanceKey = SystemConstants.SYSTEM_NAME_FOR_LOCAL_INSTANCE;
        }

        return appInstanceKey;
    }

    @Override
    public boolean isTesting() {
        return config.getBooleanConfig("application.testing");
    }

    @Override
    public Environment getEnvironment() {
        return Environment.valueOf(config.getStringConfig("application.environment"));
    }

    @Override
    public boolean isLocalMachine() {
        return getAppInstanceKey().startsWith("local");
    }

}
