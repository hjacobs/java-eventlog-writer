package de.zalando.zomcat.configuration;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import de.zalando.zomcat.SystemConstants;

/**
 * For security reasons, sensitive credentials like database passwords or encryption keys should neither be stored in
 * the application.properties nor given as command line arguments in a production environment. This class aims to
 * provides secure access to these credentials.
 *
 * @author  mjuenemann
 */
public class CredentialStore {

    private final PropertiesConfiguration config;

    protected CredentialStore(final PropertiesConfiguration config) {
        this.config = config;
    }

    /**
     * Get a credential from the CredentialStore.
     *
     * @param   key  key to identify the credential
     *
     * @return  String or null if no entry was found
     */
    public String getCredential(final String key) {
        Object value = config.getProperty(key);

        if (value == null) {
            return null;
        }

        return value.toString();
    }

    /**
     * Loads the CredentialStore. It assumes that the credentials are stored in a properties file on the local
     * filesystem and that the path to this file is given as system property by DeployCtl (something like
     * /etc/default/zomcat/pXXXX.credentials).
     *
     * @return  CredentialStore
     *
     * @throws  ConfigurationException
     */
    public static CredentialStore fromSystemEnvironment() throws ConfigurationException {
        final String path = System.getProperty(SystemConstants.SYSTEM_PROPERTY_CREDENTIALS_FILE);
        if (path == null) {
            throw new ConfigurationException("Path to credential file not found in system environment");
        }

        return new CredentialStore(new PropertiesConfiguration(path));
    }
}
