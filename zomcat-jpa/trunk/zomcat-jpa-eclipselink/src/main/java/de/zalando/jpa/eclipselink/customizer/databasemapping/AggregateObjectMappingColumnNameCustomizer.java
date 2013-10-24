package de.zalando.jpa.eclipselink.customizer.databasemapping;

import java.util.Map;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.sessions.Session;

import de.zalando.jpa.eclipselink.customizer.NameUtils;

/**
 * This ColumnNameCustomizer is for {@link Embeddable} types in {@link Entity}s.
 *
 * @author  jbellmann
 */
public class AggregateObjectMappingColumnNameCustomizer extends AbstractColumnNameCustomizer<AggregateObjectMapping> {

    public AggregateObjectMappingColumnNameCustomizer() {
        super(AggregateObjectMapping.class);
    }

    @Override
    public void customizeColumnName(final String tableName, final AggregateObjectMapping databaseMapping,
            final Session session) {

        Map<String, DatabaseField> map = databaseMapping.getAggregateToSourceFields();
        for (String key : map.keySet()) {
            DatabaseField dbField = map.get(key);
            dbField.setName(NameUtils.buildFieldName(tableName, dbField.getName()));
            logFine(session, "Set field-name for AggregatObjectMapping to " + dbField.getName());
        }

// System.out.println(map.toString());
// logFine(session, "No customization for attribute : " + databaseMapping.getAttributeName());
    }

}
