package de.zalando.zomcat.appconfig;

import de.zalando.appconfig.Configuration;

import de.zalando.domain.Environment;

import de.zalando.zomcat.configuration.AppInstanceContextProvider;
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

    AppInstanceContextProvider getAppInstanceContext();

    /**
     * Retrieve an entire entity from the configuration. All fields of the object will be retrieved as a separate
     * configuration entry with key ClassName.methodName, e.g. AppDomain.paymentMethods (get of the method name is
     * omitted in the key). The id is saved as custom context in the database, so entities can only be defined in the
     * central config service at the moment.
     *
     * @param    classOfT  class of the interface that should be implemented by the returned proxy
     * @param    id        identifier of the entity. Will be mapped to the custom context and can be null
     *
     * @return   Dynamic proxy that implements classOfT
     *
     * @see      {@link EntityConfigurationBuilder EntityConfigurationBuilder}
     * @example  PaymentMethod pm = applicationConfig.getEntity(PaymentMethod.class, pmId);
     */
    <T> T getEntity(final Class<T> classOfT, final String id);

    /**
     * Retrieve an entire entity from the configuration. All fields of the object will be retrieved as a separate
     * configuration entry with key ClassName.methodName, e.g. AppDomain.paymentMethods (get of the method name is
     * omitted in the key). The id is saved as custom context in the database, so entities can only be defined in the
     * central config service at the moment.
     *
     * @param    classOfT  class of the interface that should be implemented by the returned proxy
     * @param    id        identifier of the entity. Will be mapped to the custom context and can be null
     *
     * @return   Dynamic proxy that implements classOfT
     *
     * @see      {@link EntityConfigurationBuilder EntityConfigurationBuilder}
     * @example  PaymentMethod pm = applicationConfig.getEntity(PaymentMethod.class, pmId);
     */
    <T> T getEntity(final Class<T> classOfT, final int id);

    Configuration getConfig();

    <Toggle extends FeatureToggle> boolean isFeatureEnabled(final Toggle feature);

    <Toggle extends FeatureToggle> boolean isFeatureEnabled(final Toggle feature, final int appDomainId,
            final boolean defaultValue);

    <Toggle extends FeatureToggle> boolean isFeatureEnabled(final Toggle feature, final int appDomainId);

    <Toggle extends FeatureToggle> boolean isFeatureEnabled(final Toggle feature, final boolean defaultValue);

}
