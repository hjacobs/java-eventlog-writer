package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * @author  jbellmann
 */
public class OneToManyMappingColumnNameCustomizer extends AbstractColumnNameCustomizer<OneToManyMapping> {

    public OneToManyMappingColumnNameCustomizer() {
        super(OneToManyMapping.class);
    }

    @Override
    public void customizeColumnName(final String tableName, final OneToManyMapping databaseMapping,
            final Session session) {
        session.getSessionLog().log(SessionLog.FINE, "Do nothing on 'customizeColumnName'-method for tableName {0}",
            new Object[] {tableName}, false);
    }

}
