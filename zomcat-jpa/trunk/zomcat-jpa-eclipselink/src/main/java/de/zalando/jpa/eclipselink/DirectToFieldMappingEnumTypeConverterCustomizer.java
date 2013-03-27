package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.Session;

/**
 * @author  jbellmann
 */
public class DirectToFieldMappingEnumTypeConverterCustomizer extends AbstractConverterCustomizer<DirectToFieldMapping> {

    public DirectToFieldMappingEnumTypeConverterCustomizer() {
        super(DirectToFieldMapping.class);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void customizeConverter(final DirectToFieldMapping databaseMapping, final Session session) {
        Class attributeClass = databaseMapping.getAttributeClassification();
        if (!Enum.class.isAssignableFrom(attributeClass)) {
            return;
        }

        session.getSessionLog().log(SessionLog.FINE, "Set converter to field '{0}' with class '{1}'",
            new Object[] {databaseMapping.getFieldName(), attributeClass.getName()}, false);
        databaseMapping.setConverter(new EnumTypeConverter(attributeClass,
                databaseMapping.getField().getColumnDefinition()));
    }

}
