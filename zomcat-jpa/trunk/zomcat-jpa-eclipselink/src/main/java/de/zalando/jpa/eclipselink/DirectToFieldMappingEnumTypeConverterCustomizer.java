package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author  jbellmann
 */
public class DirectToFieldMappingEnumTypeConverterCustomizer extends AbstractConverterCustomizer<DirectToFieldMapping> {

    private static final Logger LOG = LoggerFactory.getLogger(DirectToFieldMappingEnumTypeConverterCustomizer.class);

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

        LOG.debug("Set converter to field '{}' with class '{}'", databaseMapping.getFieldName(),
            attributeClass.getName());
        databaseMapping.setConverter(new EnumTypeConverter(attributeClass,
                databaseMapping.getField().getColumnDefinition()));
    }

}
