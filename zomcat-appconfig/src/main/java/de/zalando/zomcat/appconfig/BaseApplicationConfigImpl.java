package de.zalando.zomcat.appconfig;

import static com.google.common.base.Preconditions.checkState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.appconfig.ConfigCtx;
import de.zalando.appconfig.Configuration;
import de.zalando.appconfig.builder.EntityConfigurationBuilder;

import de.zalando.domain.Environment;

import de.zalando.zomcat.configuration.AppInstanceContextProvider;

/**
 * @author  hjacobs
 */
public class BaseApplicationConfigImpl extends JobConfigSourceImpl implements BaseApplicationConfig {

    private static final Logger LOG = LoggerFactory.getLogger(BaseApplicationConfigImpl.class);
    protected static final String APPLICATION_ENVIRONMENT = "application.environment";

    protected transient Configuration config;
    private transient AppInstanceContextProvider appInstanceContextProvider;

    @Override
    public Configuration getConfig() {
        return config;
    }

    public void setConfig(final Configuration config) {
        this.config = config;
    }

    /**
     * Returns context information about the running instance like version, project name etc. Does not work locally or
     * in tests, since it's using the MANIFEST.MF file from classpath
     *
     * @return  AppInstanceContextProvider
     */
    public AppInstanceContextProvider getAppInstanceContext() {
        if (appInstanceContextProvider == null) {
            appInstanceContextProvider = AppInstanceContextProvider.fromManifestOnFilesystem();
        }

        return appInstanceContextProvider;
    }

    @Override
    public String getAppInstanceKey() {
        return getAppInstanceContext().getAppInstanceKey();
    }

    /**
     * @return  never null
     *
     * @throws  IllegalArgumentException  the configured environment is invalid
     * @throws  IllegalStateException     no environment configured
     */
    @Override
    public Environment getEnvironment() {

        final Environment environmentFromManifest = getAppInstanceContext().getEnvironment();
        if (environmentFromManifest != null) {
            return environmentFromManifest;
        }

        final String environment = config.getStringConfig(BaseApplicationConfigImpl.APPLICATION_ENVIRONMENT);
        checkState(environment != null, "no application.environment found");

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

    @Override
    public <Feature extends FeatureToggle> boolean isFeatureEnabled(final Feature feature) {
        return isFeatureEnabled(feature, false);
    }

    @Override
    public <Feature extends FeatureToggle> boolean isFeatureEnabled(final Feature feature, final boolean defaultValue) {
        try {
            String value = config.getStringConfig(feature.getAppConfigName(), null,
                    Toggle.fromBoolean(defaultValue).name());
            return Toggle.fromString(value).asBoolean();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public <Feature extends FeatureToggle> boolean isFeatureEnabled(final Feature feature, final int appDomainId,
            final boolean defaultValue) {
        try {
            String value = config.getStringConfig(feature.getAppConfigName(), new ConfigCtx(appDomainId),
                    Toggle.fromBoolean(defaultValue).name());
            return Toggle.fromString(value).asBoolean();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public <Feature extends FeatureToggle> boolean isFeatureEnabled(final Feature feature, final int appDomainId) {
        return isFeatureEnabled(feature, appDomainId, false);
    }

    @Override
    public <T> T getEntity(final Class<T> classOfT, final String id) {
        return EntityConfigurationBuilder.entityConfiguration(getConfig()).withId(id).get(classOfT);
    }

    @Override
    public <T> T getEntity(final Class<T> classOfT, final int id) {
        return EntityConfigurationBuilder.entityConfiguration(getConfig()).withId(id).get(classOfT);
    }

}
