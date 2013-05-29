package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * @param   <T>
 *
 * @author  jbellmann
 */
interface DatabaseMappingLogger<T extends DatabaseMapping> {

    void logDatabaseMapping(T databaseMapping, Session session);

    Class<T> getMappingType();
}
