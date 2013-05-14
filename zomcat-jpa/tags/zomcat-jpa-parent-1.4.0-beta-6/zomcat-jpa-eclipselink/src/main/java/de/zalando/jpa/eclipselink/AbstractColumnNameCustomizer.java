package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * @param   <T>
 *
 * @author  jbellmann
 */
public abstract class AbstractColumnNameCustomizer<T extends DatabaseMapping> implements ColumnNameCustomizer<T> {

    private Class<T> supportedMappingType;

    protected AbstractColumnNameCustomizer(final Class<T> supportedMappingType) {
        this.supportedMappingType = supportedMappingType;
    }

    @Override
    public Class<T> supportedDatabaseMapping() {
        return this.supportedMappingType;
    }
}
