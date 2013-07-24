package de.zalando.jpa.config;

import org.springframework.util.Assert;

/**
 * Provides an PersistenceUnitName which will be looked up in 'META-INF/persistece.xml'-file.
 *
 * @author  jbellmann
 */
public class StandardPersistenceUnitNameProvider implements PersistenceUnitNameProvider {

    private final String persistenceUnitName;

    public static final String DEFAULT_PERSISTENCE_UNIT_NAME = "default";

    public StandardPersistenceUnitNameProvider() {
        this(DEFAULT_PERSISTENCE_UNIT_NAME);
    }

    public StandardPersistenceUnitNameProvider(final String persistenceUnitName) {
        Assert.hasText(persistenceUnitName, "PersistenceUnitName should never be null or empty");
        this.persistenceUnitName = persistenceUnitName;
    }

    @Override
    public String getPersistenceUnitName() {

        return this.persistenceUnitName;
    }

}
