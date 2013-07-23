package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;

/**
 * The {@link DefaultZomcatSessionCustomizer} registers {@link ColumnNameCustomizer}s for {@link DirectToFieldMapping},
 * {@link OneToManyMapping} and {@link ManyToOneMapping} to confirm Zalandos 'Column-Name-Requirements'.
 *
 * @author  jbellmann
 */
public class DefaultZomcatSessionCustomizer extends AbstractZomcatSessionCustomizer {

    private final ClassDescriptorCustomizer clazzDescriptorCustomizer;

    public DefaultZomcatSessionCustomizer() {
        clazzDescriptorCustomizer = new DefaultClassDescriptorCustomizer();

        // columnNames
        clazzDescriptorCustomizer.registerColumnNameCustomizer(new DirectToFieldMappingColumnNameCustomizer());
        clazzDescriptorCustomizer.registerColumnNameCustomizer(new ManyToOneMappingColumnNameCustomizer());
        clazzDescriptorCustomizer.registerColumnNameCustomizer(new OneToManyMappingColumnNameCustomizer());
        clazzDescriptorCustomizer.registerColumnNameCustomizer(new OneToOneMappingColumnNameCustomizer());
    }

    @Override
    ClassDescriptorCustomizer getClassDescriptorCustomizer() {
        return this.clazzDescriptorCustomizer;
    }

}
