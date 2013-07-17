package de.zalando.jpa.eclipselink.customizer.session;

import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;

import de.zalando.jpa.eclipselink.customizer.classdescriptor.ChangePolicyClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.classdescriptor.ClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.classdescriptor.CompositeClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.classdescriptor.DefaultClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.CustomizerRegistry;
import de.zalando.jpa.eclipselink.customizer.databasemapping.DirectToFieldMappingColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.ManyToOneMappingColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.OneToManyMappingColumnNameCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.OneToOneMappingColumnNameCustomizer;

/**
 * The {@link DefaultZomcatSessionCustomizer} registers
 * {@link de.zalando.jpa.eclipselink.customizer.databasemapping.ColumnNameCustomizer}s for {@link DirectToFieldMapping},
 * {@link OneToManyMapping} and {@link ManyToOneMapping} to confirm Zalandos 'Column-Name-Requirements'.
 *
 * @author  jbellmann
 */
public class DefaultZomcatSessionCustomizer extends AbstractZomcatSessionCustomizer {

    private final ClassDescriptorCustomizer clazzDescriptorCustomizer;

    public DefaultZomcatSessionCustomizer() {

        // create classDescriptorCustomizer, that will use the columnNameCustomizers or do other things
        clazzDescriptorCustomizer = CompositeClassDescriptorCustomizer.build(new DefaultClassDescriptorCustomizer(),
                new ChangePolicyClassDescriptorCustomizer());

        // Register ColumnNameCustomizers
        CustomizerRegistry.get().registerColumnNameCustomizer(new DirectToFieldMappingColumnNameCustomizer());
        CustomizerRegistry.get().registerColumnNameCustomizer(new ManyToOneMappingColumnNameCustomizer());
        CustomizerRegistry.get().registerColumnNameCustomizer(new OneToManyMappingColumnNameCustomizer());
        CustomizerRegistry.get().registerColumnNameCustomizer(new OneToOneMappingColumnNameCustomizer());
    }

    @Override
    ClassDescriptorCustomizer getClassDescriptorCustomizer() {
        return this.clazzDescriptorCustomizer;
    }

}
