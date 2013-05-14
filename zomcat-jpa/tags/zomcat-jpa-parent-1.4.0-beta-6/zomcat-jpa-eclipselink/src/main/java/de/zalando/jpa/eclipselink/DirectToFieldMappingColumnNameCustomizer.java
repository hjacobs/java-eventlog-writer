package de.zalando.jpa.eclipselink;

import java.lang.reflect.Field;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.Session;

import org.springframework.util.ReflectionUtils;

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

        final Class<?> fieldType = getAttributeTypeViaReflection(databaseMapping);
        if (Boolean.class.equals(fieldType) || boolean.class.equals(fieldType)) {

            newFieldName = NameUtils.buildBooleanFieldName(tableName, databaseMapping.getAttributeName());
        } else {

            // default
            newFieldName = NameUtils.buildFieldName(tableName, databaseMapping.getAttributeName());
        }

        databaseMapping.getField().setName(newFieldName);
        session.getSessionLog().log(SessionLog.FINE, "set new field-name to {0}", new Object[] {newFieldName}, false);
    }

    /**
     * Returns the Type of an attribute.
     *
     * @param   databaseMapping
     *
     * @return  type of the attribute or null if no field exist with that name
     */
    protected Class<?> getAttributeTypeViaReflection(final DirectToFieldMapping databaseMapping) {
        final String attributeName = databaseMapping.getAttributeName();
        final Class<?> relationJavaClass = databaseMapping.getDescriptor().getJavaClass();

        final Field field = ReflectionUtils.findField(relationJavaClass, attributeName);

        return field != null ? field.getType() : null;
    }
}
