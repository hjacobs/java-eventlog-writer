package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.mappings.DatabaseMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author  jbellmann
 */
final class NoOpColumnNameCustomizer implements ColumnNameCustomizer<DatabaseMapping> {

    private static final Logger LOG = LoggerFactory.getLogger(NoOpColumnNameCustomizer.class);

    @Override
    public void customizeColumnName(final String tableName, final DatabaseMapping databaseMapping) {
        LOG.warn("Do nothing for TableName : {} and DatabaseMapping-class {}", tableName,
            databaseMapping.getClass().getName());
    }

    @Override
    public Class<DatabaseMapping> supportedDatabaseMapping() {
        return DatabaseMapping.class;
    }
}
