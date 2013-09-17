package de.zalando.jpa.eclipselink.customizer.databasemapping;

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
        logFine(session, "Do nothing on 'customizeColumnName'-method for tableName {0}", tableName);
    }

}
