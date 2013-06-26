package de.zalando.jpa.eclipselink.customizer.databasemapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.persistence.Column;

import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.Session;

import org.springframework.util.ReflectionUtils;

import de.zalando.jpa.eclipselink.customizer.databasemapping.support.ColumnFieldInspector;
import de.zalando.jpa.eclipselink.customizer.databasemapping.support.EntityFieldInspector;
import de.zalando.jpa.eclipselink.customizer.databasemapping.support.NameUtils;

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

        logDatabaseMapping(databaseMapping, session);

        String newFieldName = null;
        EntityFieldInspector<?> entityFieldInspector = getFieldInspector(databaseMapping);
        if (shouldCreateBooleanFieldName(entityFieldInspector)) {

            newFieldName = NameUtils.buildBooleanFieldName(tableName, databaseMapping.getAttributeName());
        } else if (!entityFieldInspector.isNameValueSet()) {

            // default
            newFieldName = NameUtils.buildFieldName(tableName, databaseMapping.getAttributeName());
        } else if (entityFieldInspector.isNameValueSet()) {

            // column-annotation name value is set
            newFieldName = NameUtils.buildFieldName(tableName, databaseMapping.getField().getName());
        }

        databaseMapping.getField().setName(newFieldName);
        logFine(session, "set new field-name to {0}", newFieldName);

    }

    protected boolean shouldCreateBooleanFieldName(final EntityFieldInspector<?> entityFieldInspector) {
        if (Boolean.class.equals(entityFieldInspector.getFieldType())
                || boolean.class.equals(entityFieldInspector.getFieldType())) {
            if (entityFieldInspector.isNameValueSet()) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    protected String getColumnAnnotationNameValue(final Field field) {
        String result = "";
        Column columnAnnotation = field.getAnnotation(Column.class);
        if (columnAnnotation != null) {
            result = columnAnnotation.name();
        }

        return result;
    }

    protected EntityFieldInspector<? extends Annotation> getFieldInspector(final DirectToFieldMapping databaseMapping) {
        final String attributeName = databaseMapping.getAttributeName();
        final Class<?> entityClass = databaseMapping.getDescriptor().getJavaClass();

        final Field field = ReflectionUtils.findField(entityClass, attributeName);
        return new ColumnFieldInspector(field);
    }

}
