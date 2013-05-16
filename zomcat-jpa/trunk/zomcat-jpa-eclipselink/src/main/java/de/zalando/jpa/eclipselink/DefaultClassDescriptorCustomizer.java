package de.zalando.jpa.eclipselink;

import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

import com.google.common.collect.Maps;

/**
 * @author  jbellmann
 */
public class DefaultClassDescriptorCustomizer extends LogSupport implements ClassDescriptorCustomizer {

    private static final Map<Class<DatabaseMapping>, ColumnNameCustomizer<DatabaseMapping>> COLUMN_NAME_CUSTOMIZER_REGISTRY =
        Maps.newConcurrentMap();

    private static final Map<Class<DatabaseMapping>, ConverterCustomizer<DatabaseMapping>> CONVERTER_CUSTOMIZER_REGISTRY =
        Maps.newConcurrentMap();

    private static final NoOpColumnNameCustomizer NOOPCOLUMNNAMECUSTOMIZER = new NoOpColumnNameCustomizer();

    public DefaultClassDescriptorCustomizer() { }

    @Override
    public void customize(final ClassDescriptor clazzDescriptor, final Session session) {
        logFine(session, "----  Customize for entity {0} ----\n", clazzDescriptor.getJavaClassName());
        for (DatabaseMapping databaseMapping : clazzDescriptor.getMappings()) {
            logFine(session, "Field : {0}", databaseMapping.getAttributeName());

            // columnNames
            ColumnNameCustomizer<DatabaseMapping> columnNameCustomizer = getColumnNameCustomizer(databaseMapping);

            if (isNoOpCustomizer(columnNameCustomizer)) {
                logFinest(session, "No ColumnNameCustomizer found for {0}", databaseMapping.getClass().getName());
            }

            columnNameCustomizer.customizeColumnName(clazzDescriptor.getTableName(), databaseMapping, session);

            // converter
            ConverterCustomizer<DatabaseMapping> converterCustomizer = getConverterCustomizer(databaseMapping);
            if (isNoOpCustomizer(converterCustomizer)) {
                logFinest(session, "No ConverterCustomizer found for {0}", databaseMapping.getClass().getName());
            }

            converterCustomizer.customizeConverter(databaseMapping, session);

        }

        logFine(session, "----  Entity {0} customized  ----\n", clazzDescriptor.getJavaClassName());
    }

    protected boolean isNoOpCustomizer(final ColumnNameCustomizer<DatabaseMapping> columnNameCustomizer) {
        return columnNameCustomizer.getClass().isAssignableFrom(NOOPCOLUMNNAMECUSTOMIZER.getClass());
    }

    protected boolean isNoOpCustomizer(final ConverterCustomizer<DatabaseMapping> converterCustomizer) {
        return converterCustomizer.getClass().isAssignableFrom(NoOpConverterCustomizer.class);
    }

    protected ConverterCustomizer<DatabaseMapping> getConverterCustomizer(final DatabaseMapping databaseMapping) {
        ConverterCustomizer<DatabaseMapping> customizer = CONVERTER_CUSTOMIZER_REGISTRY.get(databaseMapping.getClass());
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
            return NOOPCOLUMNNAMECUSTOMIZER;
        } else {
            return customizer;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void registerColumnNameCustomizer(final ColumnNameCustomizer columnNameCustomizer) {
        COLUMN_NAME_CUSTOMIZER_REGISTRY.put(columnNameCustomizer.supportedDatabaseMapping(), columnNameCustomizer);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void registerConverterCustomizer(final ConverterCustomizer converterCustomizer) {
        CONVERTER_CUSTOMIZER_REGISTRY.put(converterCustomizer.supportedDatabaseMapping(), converterCustomizer);
    }

}
