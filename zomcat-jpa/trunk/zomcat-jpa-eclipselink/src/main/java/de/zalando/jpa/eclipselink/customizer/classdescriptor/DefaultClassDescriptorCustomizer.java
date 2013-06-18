package de.zalando.jpa.eclipselink.customizer.classdescriptor;

import java.util.Map;

import org.eclipse.persistence.annotations.ChangeTrackingType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.descriptors.changetracking.DeferredChangeDetectionPolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangeTrackingPolicy;
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

    private static final Map<Class<? extends DatabaseMapping>, ColumnNameCustomizer<DatabaseMapping>> COLUMN_NAME_CUSTOMIZER_REGISTRY =
        Maps.newConcurrentMap();

    private static final Map<Class<? extends DatabaseMapping>, ConverterCustomizer<DatabaseMapping>> CONVERTER_CUSTOMIZER_REGISTRY =
        Maps.newConcurrentMap();

    private static final NoOpColumnNameCustomizer NOOPCOLUMNNAMECUSTOMIZER = new NoOpColumnNameCustomizer();

    private static final String SET_OBJECT_CHANGE_POLICY_TO = "Set ObjectChangePolicy to {0}";
    public static final String USE_DEFAULT_CHANGE_TRACKING_POLICY = "Use default change tracking policy";
    public static final String COULD_NOT_DETERMINE_CHANGE_TRACKING_TYPE =
        "Could not determine ChangeTrackingType for property value '{0}'. Use AUTO.";

    public static final String DEFERRED_CHANGE_DETECTION_POLICY = "DeferredChangeDetectionPolicy";
    public static final String OBJECT_CHANGE_TRACKING_POLICY = "ObjectChangeTrackingPolicy";
    public static final String ATTRIBUTE_CHANGE_TRACKING_POLICY = "AttributeChangeTrackingPolicy";

    public DefaultClassDescriptorCustomizer() { }

    @Override
    public void customize(final ClassDescriptor clazzDescriptor, final Session session) {
        logFine(session, "----  Customize for entity {0} ----\n", clazzDescriptor.getJavaClassName());

        customizeObjectChangePolicy(clazzDescriptor, session);

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

    private void customizeObjectChangePolicy(final ClassDescriptor clazzDescriptor, final Session session) {
        final String propertyValue = (String) session.getProperty(ZOMCAT_JPA_CHANGE_TRACKER_TYPE);
        ChangeTrackingType changeTrackingType;

        try {
            changeTrackingType = ChangeTrackingType.valueOf(propertyValue);
        } catch (Exception e) {
            logWarning(session, COULD_NOT_DETERMINE_CHANGE_TRACKING_TYPE, propertyValue);
            changeTrackingType = ChangeTrackingType.AUTO;
        }

        switch (changeTrackingType) {

            case DEFERRED :
                clazzDescriptor.setObjectChangePolicy(new DeferredChangeDetectionPolicy());
                logFine(session, SET_OBJECT_CHANGE_POLICY_TO, DEFERRED_CHANGE_DETECTION_POLICY);
                break;

            case OBJECT :
                clazzDescriptor.setObjectChangePolicy(new ObjectChangeTrackingPolicy());
                logFine(session, SET_OBJECT_CHANGE_POLICY_TO, OBJECT_CHANGE_TRACKING_POLICY);
                break;

            case ATTRIBUTE :
                clazzDescriptor.setObjectChangePolicy(new AttributeChangeTrackingPolicy());
                logFine(session, SET_OBJECT_CHANGE_POLICY_TO, ATTRIBUTE_CHANGE_TRACKING_POLICY);
                break;

            case AUTO :
            default :
                logFine(session, USE_DEFAULT_CHANGE_TRACKING_POLICY);
        }
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
