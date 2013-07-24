package de.zalando.jpa.eclipselink.customizer.classdescriptor;

import java.util.Map;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

import com.google.common.collect.Maps;

import de.zalando.jpa.eclipselink.LogSupport;
import de.zalando.jpa.eclipselink.customizer.databasemapping.ColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.ConverterCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.NoOpColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.NoOpConverterCustomizer;

/**
 * @author  jbellmann
 */
public class DefaultClassDescriptorCustomizer extends LogSupport implements ClassDescriptorCustomizer {

    private final Map<Class<? extends DatabaseMapping>, ColumnNameCustomizer<DatabaseMapping>> columnNameCustomizerRegistry =
        Maps.newConcurrentMap();

    private final Map<Class<? extends DatabaseMapping>, ConverterCustomizer<DatabaseMapping>> converterCustomizerRegistry =
        Maps.newConcurrentMap();

    private final NoOpColumnNameCustomizer noOpColumnNameCustomizer = new NoOpColumnNameCustomizer();

    /**
     * Default.
     */
    public DefaultClassDescriptorCustomizer() { }

    @Override
    public void customize(final ClassDescriptor clazzDescriptor, final Session session) {
        logFine(session, START_CUS, clazzDescriptor.getJavaClassName());

        for (DatabaseMapping databaseMapping : clazzDescriptor.getMappings()) {
            logFine(session, FIELD, databaseMapping.getAttributeName());

            // columnNames
            ColumnNameCustomizer<DatabaseMapping> columnNameCustomizer = getColumnNameCustomizer(databaseMapping);

            if (isNoOpCustomizer(columnNameCustomizer)) {
                logFinest(session, NO_COL_CUSTOMIZER, databaseMapping.getClass().getName());
            }

            columnNameCustomizer.customizeColumnName(clazzDescriptor.getTableName(), databaseMapping, session);

            // converter
            ConverterCustomizer<DatabaseMapping> converterCustomizer = getConverterCustomizer(databaseMapping);
            if (isNoOpCustomizer(converterCustomizer)) {
                logFinest(session, NO_CONV_CUSTOMIZER, databaseMapping.getClass().getName());
            }

            converterCustomizer.customizeConverter(databaseMapping, session);

        }

        // session.getProject().addPartitioningPolicy(null);

        logFine(session, END_CUS, clazzDescriptor.getJavaClassName());
    }

    protected boolean isNoOpCustomizer(final ColumnNameCustomizer<DatabaseMapping> columnNameCustomizer) {
        return columnNameCustomizer.getClass().isAssignableFrom(NoOpColumnNameCustomizer.class);
    }

    protected boolean isNoOpCustomizer(final ConverterCustomizer<DatabaseMapping> converterCustomizer) {
        return converterCustomizer.getClass().isAssignableFrom(NoOpConverterCustomizer.class);
    }

    /**
     * Registers an {@link ColumnNameCustomizer} in this registry by using the supported {@link DatabaseMapping}-class
     * as key.
     *
     * @param  columnNameCustomizer
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void registerColumnNameCustomizer(final ColumnNameCustomizer columnNameCustomizer) {
        columnNameCustomizerRegistry.put(columnNameCustomizer.supportedDatabaseMapping(), columnNameCustomizer);
    }

    /**
     * Registers an {@link ConverterCustomizer} in this registry by using the supported {@link DatabaseMapping}-class as
     * key.
     *
     * @param  converterCustomizer
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void registerConverterCustomizer(final ConverterCustomizer converterCustomizer) {
        converterCustomizerRegistry.put(converterCustomizer.supportedDatabaseMapping(), converterCustomizer);
    }

    /**
     * Returns an {@link ConverterCustomizer} for the {@link DatabaseMapping} argument. If no
     * {@link ConverterCustomizer} is registered for this {@link DatabaseMapping} it will return an
     * {@link NoOpConverterCustomizer}. So it should never return null.
     *
     * @param   databaseMapping
     *
     * @return  {@link ConverterCustomizer} for the {@link DatabaseMapping}, otherwise an
     *          {@link NoOpConverterCustomizer}, should never return null.
     */
    protected ConverterCustomizer<DatabaseMapping> getConverterCustomizer(final DatabaseMapping databaseMapping) {
        ConverterCustomizer<DatabaseMapping> customizer = converterCustomizerRegistry.get(databaseMapping.getClass());
        if (customizer == null) {
            return new NoOpConverterCustomizer();
        } else {
            return customizer;
        }
    }

    /**
     * Returns an {@link ColumnNameCustomizer} for the {@link DatabaseMapping} argument. If no
     * {@link ColumnNameCustomizer} is registered for this {@link DatabaseMapping} it will return an
     * {@link NoOpColumnNameCustomizer}. So it should never return null.
     *
     * @param   databaseMapping
     *
     * @return  {@link ColumnNameCustomizer} for the {@link DatabaseMapping}, otherwise an
     *          {@link NoOpColumnNameCustomizer}, should never return null.
     */
    protected ColumnNameCustomizer<DatabaseMapping> getColumnNameCustomizer(final DatabaseMapping databaseMapping) {
        ColumnNameCustomizer<DatabaseMapping> customizer = columnNameCustomizerRegistry.get(databaseMapping.getClass());
        if (customizer == null) {
            return this.noOpColumnNameCustomizer;
        } else {
            return customizer;
        }
    }

}
