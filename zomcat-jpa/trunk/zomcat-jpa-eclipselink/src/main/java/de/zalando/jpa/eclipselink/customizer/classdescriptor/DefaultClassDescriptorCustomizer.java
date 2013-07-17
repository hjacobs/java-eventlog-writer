package de.zalando.jpa.eclipselink.customizer.classdescriptor;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

import de.zalando.jpa.eclipselink.LogSupport;
import de.zalando.jpa.eclipselink.customizer.databasemapping.ColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.ConverterCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.CustomizerRegistry;
import de.zalando.jpa.eclipselink.customizer.databasemapping.NoOpColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.NoOpConverterCustomizer;

/**
 * @author  jbellmann
 */
public class DefaultClassDescriptorCustomizer extends LogSupport implements ClassDescriptorCustomizer {

    /**
     * Default.
     */
    public DefaultClassDescriptorCustomizer() { }

    @Override
    public void customize(final ClassDescriptor clazzDescriptor, final Session session) {
        logFine(session, START_CUS, clazzDescriptor.getJavaClassName());

// customizeObjectChangePolicy(clazzDescriptor, session);

        for (DatabaseMapping databaseMapping : clazzDescriptor.getMappings()) {
            logFine(session, FIELD, databaseMapping.getAttributeName());

            // columnNames
            ColumnNameCustomizer<DatabaseMapping> columnNameCustomizer = CustomizerRegistry.get()
                                                                                           .getColumnNameCustomizer(
                                                                                               databaseMapping);

            if (isNoOpCustomizer(columnNameCustomizer)) {
                logFinest(session, NO_COL_CUSTOMIZER, databaseMapping.getClass().getName());
            }

            columnNameCustomizer.customizeColumnName(clazzDescriptor.getTableName(), databaseMapping, session);

            // converter
            ConverterCustomizer<DatabaseMapping> converterCustomizer = CustomizerRegistry.get().getConverterCustomizer(
                    databaseMapping);
            if (isNoOpCustomizer(converterCustomizer)) {
                logFinest(session, NO_CONV_CUSTOMIZER, databaseMapping.getClass().getName());
            }

            converterCustomizer.customizeConverter(databaseMapping, session);

        }

        // session.getProject().addPartitioningPolicy(null);

        logFine(session, END_CUS, clazzDescriptor.getJavaClassName());
    }

/*    private void customizeObjectChangePolicy(final ClassDescriptor clazzDescriptor, final Session session) {
 *      final String propertyValue = (String) session.getProperty(ZOMCAT_JPA_CHANGE_TRACKER_TYPE);
 *
 *      ChangeTrackingType changeTrackingType = ChangeTrackingType.AUTO;
 *
 *      if (propertyValue != null && (!propertyValue.trim().isEmpty())) {
 *          try {
 *              changeTrackingType = ChangeTrackingType.valueOf(propertyValue);
 *          } catch (Exception e) {
 *              logWarning(session, COULD_NOT_DETERMINE_CHANGE_TRACKING_TYPE, propertyValue);
 *              changeTrackingType = ChangeTrackingType.AUTO;
 *          }
 *      }
 *
 *      switch (changeTrackingType) {
 *
 *          case DEFERRED :
 *              clazzDescriptor.setObjectChangePolicy(new DeferredChangeDetectionPolicy());
 *              logFine(session, SET_OBJECT_CHANGE_POLICY_TO, DEFERRED_CHANGE_DETECTION_POLICY);
 *              break;
 *
 *          case OBJECT :
 *              clazzDescriptor.setObjectChangePolicy(new ObjectChangeTrackingPolicy());
 *              logFine(session, SET_OBJECT_CHANGE_POLICY_TO, OBJECT_CHANGE_TRACKING_POLICY);
 *              break;
 *
 *          case ATTRIBUTE :
 *              clazzDescriptor.setObjectChangePolicy(new AttributeChangeTrackingPolicy());
 *              logFine(session, SET_OBJECT_CHANGE_POLICY_TO, ATTRIBUTE_CHANGE_TRACKING_POLICY);
 *              break;
 *
 *          case AUTO :
 *          default :
 *              logFine(session, USE_DEFAULT_CHANGE_TRACKING_POLICY);
 *      }
 *  }*/

    protected boolean isNoOpCustomizer(final ColumnNameCustomizer<DatabaseMapping> columnNameCustomizer) {
        return columnNameCustomizer.getClass().isAssignableFrom(NoOpColumnNameCustomizer.class);
    }

    protected boolean isNoOpCustomizer(final ConverterCustomizer<DatabaseMapping> converterCustomizer) {
        return converterCustomizer.getClass().isAssignableFrom(NoOpConverterCustomizer.class);
    }

}
