package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.mappings.DirectToFieldMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Supports Column-Name-Customization for DirectToFieldMappings.
 *
 * @author  jbellmann
 */
public class DirectToFieldMappingColumnNameCustomizer extends AbstractColumnNameCustomizer<DirectToFieldMapping> {

    private static final Logger LOG = LoggerFactory.getLogger(DirectToFieldMappingColumnNameCustomizer.class);

    public DirectToFieldMappingColumnNameCustomizer() {
        super(DirectToFieldMapping.class);
    }

    @Override
    public void customizeColumnName(final String tableName, final DirectToFieldMapping databaseMapping) {
        final String newFieldName = NameUtils.buildFieldName(tableName, databaseMapping.getAttributeName());
        databaseMapping.getField().setName(newFieldName);
        LOG.debug("set new field-name to {}", newFieldName);
    }
}
