package de.zalando.jpa.eclipselink.customizer.databasemapping;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * Defines a ColumnNameCustomizer. A {@link ColumnNameCustomizer} does only one thing, customize the column-names for an
 * specific {@link DatabaseMapping}.
 *
 * @param   <T>
 *
 * @author  jbellmann
 */
public interface ColumnNameCustomizer<T extends DatabaseMapping> {

    void customizeColumnName(String tableName, T databaseMapping, Session session);

    Class<T> supportedDatabaseMapping();

}
