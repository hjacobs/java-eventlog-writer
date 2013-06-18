package de.zalando.jpa.eclipselink.customizer.classdescriptor;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Session;

import de.zalando.jpa.eclipselink.customizer.databasemapping.ColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.ConverterCustomizer;

/**
 * Defines an {@link ClassDescriptorCustomizer}.<br/>
 * With an {@link ClassDescriptor} we can customize field-and association-mappings for one class/entity/type.
 *
 * @author  jbellmann
 */
public interface ClassDescriptorCustomizer {

    String ZOMCAT_JPA_CHANGE_TRACKER_TYPE = "zomcatJpa.changeTracker.type";

    void customize(ClassDescriptor clazzDescriptor, Session session);

    void registerColumnNameCustomizer(ColumnNameCustomizer columnNameCustomizer);

    void registerConverterCustomizer(ConverterCustomizer converterCustomizer);

}
