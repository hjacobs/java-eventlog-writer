package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * @author  jbellmann
 */
public class NoOpConverterCustomizer implements ConverterCustomizer<DatabaseMapping> {

    @Override
    public void customizeConverter(final DatabaseMapping databaseMapping, final Session session) {
        session.getSessionLog().log(SessionLog.FINE, "Do not customize converter on databaseMapping for field {0} ",
            new Object[] {databaseMapping.getAttributeName()}, false);
    }

    @Override
    public Class<DatabaseMapping> supportedDatabaseMapping() {
        return DatabaseMapping.class;
    }
}
