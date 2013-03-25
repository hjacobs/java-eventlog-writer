package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * Defines a ColumnNameCustomizer. A {@link ColumnNameCustomizer} does only one thing, customize the column-names for an
 * specific {@link DatabaseMapping}.
 *
 * @param   <T>
 *
 * @author  jbellmann
 */
public interface ColumnNameCustomizer<T extends DatabaseMapping> {

    void customizeColumnName(String tableName, T databaseMapping);

    Class<T> supportedDatabaseMapping();

}
