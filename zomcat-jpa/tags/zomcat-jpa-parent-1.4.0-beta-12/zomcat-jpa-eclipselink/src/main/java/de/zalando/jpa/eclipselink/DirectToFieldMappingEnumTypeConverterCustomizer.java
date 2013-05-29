package de.zalando.jpa.eclipselink;

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

        if (hasEnumTypeConverter(databaseMapping)) {

            final EnumTypeConverter eclipseConverter = (EnumTypeConverter) databaseMapping.getConverter();
            final Class enumClazz = eclipseConverter.getEnumClass();

            logFine(session, "Set enum-converter to field {0} with class {1}", databaseMapping.getField().getName(),
                enumClazz.getName());

            databaseMapping.setConverter(new de.zalando.jpa.eclipselink.EnumTypeConverter(enumClazz,
                    databaseMapping.getField().getColumnDefinition()));

        }

    }

    protected boolean hasEnumTypeConverter(final DirectToFieldMapping databaseMapping) {

        if (databaseMapping.getConverter() == null) {
            return false;
        }

        return EnumTypeConverter.class.equals(databaseMapping.getConverter().getClass());
    }

}
