package de.zalando.jpa.eclipselink;

import java.lang.reflect.Field;

import javax.persistence.Column;

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

        logFine(session, "\tmapping.attributeName : {0}", databaseMapping.getAttributeName());
        logFine(session, "\tmapping.attributeClassification: {0}", databaseMapping.getAttributeClassification());
        logFine(session, "\tmapping.field.name : {0}", databaseMapping.getField().getName());
        logFine(session, "\tmapping.field.sqlType : {0}", databaseMapping.getField().getSqlType());
        logFine(session, "\tmapping.field.typeName: {0}", databaseMapping.getField().getTypeName());
        logFine(session, "\tmapping.field.columnDefinition : {0}", databaseMapping.getField().getColumnDefinition());
        logFine(session, "\tmapping.fieldClassfication : {0}", databaseMapping.getFieldClassification());
        logFine(session, "\tmapping.field.sqlType : {0}", databaseMapping.getField().getSqlType());

        final AttributeInfo attributeInfo = getAttributeInfoViaReflection(databaseMapping);
        if (shouldCreateBooleanFieldName(attributeInfo)) {

            newFieldName = NameUtils.buildBooleanFieldName(tableName, databaseMapping.getAttributeName());
        } else if (!attributeInfo.isColumnAnnotationNameValueSet()) {

            // default
            newFieldName = NameUtils.buildFieldName(tableName, databaseMapping.getAttributeName());
        } else if (attributeInfo.isColumnAnnotationNameValueSet()) {

            // column-annotation name value is set
            newFieldName = NameUtils.buildFieldName(tableName, databaseMapping.getField().getName());
        }

        databaseMapping.getField().setName(newFieldName);
        logFine(session, "set new field-name to {0}", newFieldName);

    }

    protected void logFine(final Session session, final String message, final Object... args) {
        session.getSessionLog().log(SessionLog.FINE, message, args, false);
    }

    protected void logFine(final Session session, final String message) {
        logFine(session, message, new Object[] {});
    }

    protected boolean shouldCreateBooleanFieldName(final AttributeInfo attributeInfo) {
        if (Boolean.class.equals(attributeInfo.getAttributeType())
                || boolean.class.equals(attributeInfo.getAttributeType())) {
            if (attributeInfo.isColumnAnnotationNameValueSet()) {
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

    protected AttributeInfo getAttributeInfoViaReflection(final DirectToFieldMapping databaseMapping) {

        final String attributeName = databaseMapping.getAttributeName();
        final Class<?> relationJavaClass = databaseMapping.getDescriptor().getJavaClass();

        final Field field = ReflectionUtils.findField(relationJavaClass, attributeName);
        final Class<?> fieldType = field.getType();
        final boolean nameValueExist = "".equals(getColumnAnnotationNameValue(field)) ? false : true;

        return new AttributeInfo(fieldType, nameValueExist);
    }

    /**
     * Simple Value-Object.
     *
     * @author  jbellmann
     */
    static class AttributeInfo {

        private final Class<?> attributeType;
        private final boolean columnAnnotationNameValueSet;

        AttributeInfo(final Class<?> attributeType, final boolean columnAnnotationNameValueSet) {
            this.attributeType = attributeType;
            this.columnAnnotationNameValueSet = columnAnnotationNameValueSet;
        }

        /**
         * @return  the type of the attibute
         */
        Class<?> getAttributeType() {
            return attributeType;
        }

        /**
         * @return  true if the attribute is annotated with {@link Column} and {@link Column#name} is not null or empty.
         */
        boolean isColumnAnnotationNameValueSet() {
            return columnAnnotationNameValueSet;
        }

    }
}
