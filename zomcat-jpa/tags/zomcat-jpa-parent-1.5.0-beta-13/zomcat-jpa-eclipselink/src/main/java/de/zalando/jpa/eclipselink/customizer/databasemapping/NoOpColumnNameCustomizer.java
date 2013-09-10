package de.zalando.jpa.eclipselink.customizer.databasemapping;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * @author  jbellmann
 */
public final class NoOpColumnNameCustomizer implements ColumnNameCustomizer<DatabaseMapping> {

    @Override
    public void customizeColumnName(final String tableName, final DatabaseMapping databaseMapping,
            final Session session) {
        session.getSessionLog().log(SessionLog.FINE,
            "Do not customize for TableName : {0} and DatabaseMapping-class {1}",
            new Object[] {tableName, databaseMapping.getClass().getName()}, false);
    }

    @Override
    public Class<DatabaseMapping> supportedDatabaseMapping() {
        return DatabaseMapping.class;
    }
}
