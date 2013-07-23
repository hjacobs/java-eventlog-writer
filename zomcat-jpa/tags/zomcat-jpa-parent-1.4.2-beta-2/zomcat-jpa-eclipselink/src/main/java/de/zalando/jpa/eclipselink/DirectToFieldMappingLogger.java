package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * Logs informations about the {@link DatabaseMapping}.
 *
 * @author  jbellmann
 */
class DirectToFieldMappingLogger extends LogSupport implements DatabaseMappingLogger<DirectToFieldMapping> {

    @Override
    public void logDatabaseMapping(final DirectToFieldMapping databaseMapping, final Session session) {

        logFine(session, "\tmapping.attributeName : {0}", databaseMapping.getAttributeName());
        logFine(session, "\tmapping.attributeClassification: {0}", databaseMapping.getAttributeClassification());
        logFine(session, "\tmapping.field.name : {0}", databaseMapping.getField().getName());
        logFine(session, "\tmapping.field.sqlType : {0}", databaseMapping.getField().getSqlType());
        logFine(session, "\tmapping.field.typeName: {0}", databaseMapping.getField().getTypeName());
        logFine(session, "\tmapping.field.columnDefinition : {0}", databaseMapping.getField().getColumnDefinition());
        logFine(session, "\tmapping.fieldClassfication : {0}", databaseMapping.getFieldClassification());
    }

    @Override
    public Class<DirectToFieldMapping> getMappingType() {
        return DirectToFieldMapping.class;
    }
}
