package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.converters.EnumTypeConverter;
import org.eclipse.persistence.sessions.Session;

/**
 * @author  jbellmann
 */
public class DirectToFieldMappingEnumTypeConverterCustomizer extends AbstractConverterCustomizer<DirectToFieldMapping> {

    public DirectToFieldMappingEnumTypeConverterCustomizer() {
        super(DirectToFieldMapping.class);
    }

    @Override
    public void customizeConverter(final DirectToFieldMapping databaseMapping, final Session session) {

        if (databaseMapping.getConverter() == null) {
            return;
        }

        if (databaseMapping.getConverter().getClass().equals(EnumTypeConverter.class)) {

            final EnumTypeConverter eclipseConverter = (EnumTypeConverter) databaseMapping.getConverter();
            final Class enumClazz = eclipseConverter.getEnumClass();

            session.getSessionLog().log(SessionLog.FINE, "Set enum-converter to field {0} with class {1}",
                new Object[] {databaseMapping.getField().getName(), enumClazz.getName()}, false);

            databaseMapping.setConverter(new de.zalando.jpa.eclipselink.EnumTypeConverter(enumClazz,
                    databaseMapping.getField().getColumnDefinition()));

        }

    }

}
