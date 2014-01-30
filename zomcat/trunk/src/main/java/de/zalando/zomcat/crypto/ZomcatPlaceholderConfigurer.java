package de.zalando.zomcat.crypto;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import org.springframework.core.env.ConfigurablePropertyResolver;

import org.springframework.util.StringValueResolver;

import de.zalando.domain.Environment;

import de.zalando.zomcat.configuration.AppInstanceContextProvider;

/**
 * This placeholder configurer will decrypt placeholder values starting with zomcat:crypto using the ZomcatCryptoUtil.
 * Properties can be overwritten by the system environment (e.g. command line parameters) by default.
 *
 * @author  mjuenemann
 */
public class ZomcatPlaceholderConfigurer extends PropertySourcesPlaceholderConfigurer {
    private static final String CRYTO_PREFIX = "zomcat:crypto:";

    /**
     * Similar to PropertySourcesPlaceholderConfigurer.processProperties, but with decryption.
     */
    @Override
    protected void processProperties(final ConfigurableListableBeanFactory beanFactoryToProcess,
            final ConfigurablePropertyResolver propertyResolver) throws BeansException {
        final ZomcatCryptoUtil zomcatCrypto = new ZomcatCryptoUtil();
        final AppInstanceContextProvider appInstanceContextProvider = AppInstanceContextProvider
                .fromManifestOnFilesystem();
        final Environment environment = appInstanceContextProvider.getEnvironment();
        final String projectName = appInstanceContextProvider.getProjectName();

        propertyResolver.setPlaceholderPrefix(this.placeholderPrefix);
        propertyResolver.setPlaceholderSuffix(this.placeholderSuffix);
        propertyResolver.setValueSeparator(this.valueSeparator);

        StringValueResolver valueResolver = new StringValueResolver() {
            public String resolveStringValue(final String strVal) {
                String resolved = ignoreUnresolvablePlaceholders ? propertyResolver.resolvePlaceholders(strVal)
                                                                 : propertyResolver.resolveRequiredPlaceholders(strVal);

                if (resolved.startsWith(CRYTO_PREFIX)) {
                    try {
                        if (environment == null || projectName == null) {
                            throw new ZomcatCryptoException("Found a property with prefix " + CRYTO_PREFIX
                                    + ", but could not determine environment and project name. Note that encrypted properties are not supported when running locally.");
                        }

                        resolved = zomcatCrypto.decrypt(resolved.substring(CRYTO_PREFIX.length()), projectName,
                                environment);
                    } catch (ZomcatCryptoException e) {
                        throw new RuntimeException(e);
                    }
                }

                return (resolved.equals(nullValue) ? null : resolved);
            }
        };

        doProcessProperties(beanFactoryToProcess, valueResolver);
    }
}
