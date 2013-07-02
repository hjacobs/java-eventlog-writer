package de.zalando.jpa.config;

import org.springframework.util.Assert;

/**
 * @author  jbellmann
 */
public class DefaultPersistenceUnitNameProvider implements PersistenceUnitNameProvider {

    private final String persistenceUnitName;

    public DefaultPersistenceUnitNameProvider(final String persistenceUnitName) {
        Assert.hasText(persistenceUnitName, "PersistenceUnitName should never be null or empty");
        this.persistenceUnitName = persistenceUnitName;
    }

    @Override
    public String getPersistenceUnitName() {

        return this.persistenceUnitName;
    }

}
