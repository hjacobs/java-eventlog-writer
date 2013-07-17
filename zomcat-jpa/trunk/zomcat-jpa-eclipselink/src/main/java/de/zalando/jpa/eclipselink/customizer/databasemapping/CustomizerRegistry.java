package de.zalando.jpa.eclipselink.customizer.databasemapping;

import java.util.Map;

import org.eclipse.persistence.mappings.DatabaseMapping;

import com.google.common.collect.Maps;

import de.zalando.jpa.eclipselink.customizer.classdescriptor.ClassDescriptorCustomizer;

/**
 * To register {@link ColumnNameCustomizer} and {@link ConverterCustomizer} to be used in
 * {@link ClassDescriptorCustomizer}s.
 *
 * @author  jbellmann
 */
public final class CustomizerRegistry {

    private static CustomizerRegistry registryInstance;

    private final Map<Class<? extends DatabaseMapping>, ColumnNameCustomizer<DatabaseMapping>> columnNameCustomizerRegistry =
        Maps.newConcurrentMap();

    private final Map<Class<? extends DatabaseMapping>, ConverterCustomizer<DatabaseMapping>> converterCustomizerRegistry =
        Maps.newConcurrentMap();

    private final NoOpColumnNameCustomizer noOpColumnNameCustomizer = new NoOpColumnNameCustomizer();

    static {
        registryInstance = new CustomizerRegistry();
    }

    private CustomizerRegistry() {
        // hide constructor
    }

    public static CustomizerRegistry get() {
        return registryInstance;
    }

    /**
     * Clears underlying maps. Can be called if Session is fully customized to clean up.
     */
    public void clear() {
        this.columnNameCustomizerRegistry.clear();
        this.converterCustomizerRegistry.clear();
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
    public ConverterCustomizer<DatabaseMapping> getConverterCustomizer(final DatabaseMapping databaseMapping) {
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
    public ColumnNameCustomizer<DatabaseMapping> getColumnNameCustomizer(final DatabaseMapping databaseMapping) {
        ColumnNameCustomizer<DatabaseMapping> customizer = columnNameCustomizerRegistry.get(databaseMapping.getClass());
        if (customizer == null) {
            return this.noOpColumnNameCustomizer;
        } else {
            return customizer;
        }
    }
}
