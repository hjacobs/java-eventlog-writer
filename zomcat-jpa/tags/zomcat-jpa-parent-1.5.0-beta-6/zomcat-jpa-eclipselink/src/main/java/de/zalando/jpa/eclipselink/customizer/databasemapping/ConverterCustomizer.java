package de.zalando.jpa.eclipselink.customizer.databasemapping;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * @param   <T>
 *
 * @author  jbellmann
 */
public interface ConverterCustomizer<T extends DatabaseMapping> {

    void customizeConverter(T databaseMapping, Session session);

    Class<T> supportedDatabaseMapping();

}
