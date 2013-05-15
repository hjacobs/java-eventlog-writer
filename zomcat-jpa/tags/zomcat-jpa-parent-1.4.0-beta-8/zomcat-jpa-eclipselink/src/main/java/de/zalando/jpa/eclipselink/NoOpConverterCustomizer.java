package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * @author  jbellmann
 */
public class NoOpConverterCustomizer extends AbstractCustomizer implements ConverterCustomizer<DatabaseMapping> {

    @Override
    public void customizeConverter(final DatabaseMapping databaseMapping, final Session session) {
        logFinest(session, "Do not customize converter on databaseMapping for field {0} ",
            databaseMapping.getAttributeName());
    }

    @Override
    public Class<DatabaseMapping> supportedDatabaseMapping() {
        return DatabaseMapping.class;
    }
}
