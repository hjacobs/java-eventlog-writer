package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * Supports Column-Name-Customization for DirectToFieldMappings.
 *
 * @author  jbellmann
 */
public class DirectToFieldMappingColumnNameCustomizer extends AbstractColumnNameCustomizer<DirectToFieldMapping> {

    public DirectToFieldMappingColumnNameCustomizer() {
        super(DirectToFieldMapping.class);
    }

    @Override
    public void customizeColumnName(final String tableName, final DirectToFieldMapping databaseMapping,
            final Session session) {
        String newFieldName = null;
        if (Boolean.class.equals(databaseMapping.getAttributeClassification())) {
            newFieldName = NameUtils.buildBooleanFieldName(tableName, databaseMapping.getAttributeName());
        } else {
            newFieldName = NameUtils.buildFieldName(tableName, databaseMapping.getAttributeName());
        }

        databaseMapping.getField().setName(newFieldName);
        session.getSessionLog().log(SessionLog.FINE, "set new field-name to {0}", new Object[] {newFieldName}, false);
    }
}
