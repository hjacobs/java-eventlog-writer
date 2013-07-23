package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * @param   <T>
 *
 * @author  jbellmann
 */
public abstract class AbstractConverterCustomizer<T extends DatabaseMapping> extends LogSupport
    implements ConverterCustomizer<T> {

    private final Class<T> supportedDatabaseMapping;

    protected AbstractConverterCustomizer(final Class<T> databaseMapping) {

        this.supportedDatabaseMapping = databaseMapping;
    }

    @Override
    public Class<T> supportedDatabaseMapping() {

        return this.supportedDatabaseMapping;
    }

}
