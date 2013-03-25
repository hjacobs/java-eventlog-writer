package de.zalando.jpa.eclipselink;

import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * @author  jbellmann
 */
public class DefaultClassDescriptorCustomizer implements ClassDescriptorCustomizer {

// private static final Logger LOG = LoggerFactory.getLogger(DefaultClassDescriptorCustomizer.class);

    private SessionLog sessionLog = null;

    private static final Map<Class<DatabaseMapping>, ColumnNameCustomizer<DatabaseMapping>> COLUMN_NAME_CUSTOMIZER_REGISTRY =
        Maps.newConcurrentMap();

    private static final Map<Class<DatabaseMapping>, ConverterCustomizer<DatabaseMapping>> CONVERTER_CUSTOMIZER_REGISTRY =
        Maps.newConcurrentMap();

    private static final NoOpColumnNameCustomizer NOOPCOLUMNNAMECUSTOMIZER = new NoOpColumnNameCustomizer();

    public DefaultClassDescriptorCustomizer() { }

    @Override
    public void customize(final ClassDescriptor clazzDescriptor, final Session session) {
        this.sessionLog = session.getSessionLog();
        Preconditions.checkNotNull(this.sessionLog, "SessionLog should never be null");
        for (DatabaseMapping databaseMapping : clazzDescriptor.getMappings()) {

            // columnNames
            ColumnNameCustomizer<DatabaseMapping> columnNameCustomizer = getColumnNameCustomizer(databaseMapping);
            columnNameCustomizer.customizeColumnName(clazzDescriptor.getTableName(), databaseMapping, session);

            // converter
            ConverterCustomizer<DatabaseMapping> converterCustomizer = getConverterCustomizer(databaseMapping);
            converterCustomizer.customizeConverter(databaseMapping, session);
        }
    }

    protected ConverterCustomizer<DatabaseMapping> getConverterCustomizer(final DatabaseMapping databaseMapping) {
        ConverterCustomizer<DatabaseMapping> customizer = CONVERTER_CUSTOMIZER_REGISTRY.get(databaseMapping);
        if (customizer == null) {
            return new NoOpConverterCustomizer();
        } else {
            return customizer;
        }
    }

    protected ColumnNameCustomizer<DatabaseMapping> getColumnNameCustomizer(final DatabaseMapping databaseMapping) {
        ColumnNameCustomizer<DatabaseMapping> customizer = COLUMN_NAME_CUSTOMIZER_REGISTRY.get(
                databaseMapping.getClass());
        if (customizer == null) {
            sessionLog.log(SessionLog.FINE, "No ColumnNameCustomizer found for {0}, return NoOpColumnNameCustomizer",
                new Object[] {databaseMapping.getClass().getName()}, false);
            return NOOPCOLUMNNAMECUSTOMIZER;
        } else {
            return customizer;
        }
    }

    @Override
    public void registerColumnNameCustomizer(final ColumnNameCustomizer columnNameCustomizer) {
        COLUMN_NAME_CUSTOMIZER_REGISTRY.put(columnNameCustomizer.supportedDatabaseMapping(), columnNameCustomizer);
    }

    @Override
    public void registerConverterCustomizer(final ConverterCustomizer converterCustomizer) {
        CONVERTER_CUSTOMIZER_REGISTRY.put(converterCustomizer.supportedDatabaseMapping(), converterCustomizer);
    }

    @VisibleForTesting
    protected void setSessionLog(final SessionLog sessionLog) {
        this.sessionLog = sessionLog;
    }

}
